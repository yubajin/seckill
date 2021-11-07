package cn.yubajin.seckill.controller;


import cn.yubajin.seckill.Exception.GlobalException;
import cn.yubajin.seckill.common.AccessLimit;
import cn.yubajin.seckill.pojo.SeckillGoods;
import cn.yubajin.seckill.pojo.SeckillMessage;
import cn.yubajin.seckill.pojo.User;
import cn.yubajin.seckill.rabbitmq.MQSender;
import cn.yubajin.seckill.service.IGoodsService;
import cn.yubajin.seckill.service.IOrderService;
import cn.yubajin.seckill.service.ISeckillOrderService;
import cn.yubajin.seckill.utils.JsonUtil;
import cn.yubajin.seckill.vo.GoodsVo;
import cn.yubajin.seckill.vo.RespBean;
import cn.yubajin.seckill.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yubj17
 * @since 2021-10-31
 */
@Controller
@RequestMapping("/seckill")
@Slf4j
public class SeckillOrderController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private DefaultRedisScript<Long> script;

    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if (null==user||goodsId<0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // 设置请求头为输出图片类型
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码，将结果放入redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败",e.getMessage());
        }
    }

    /**
     * 获取秒杀地址
     *
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        // @AccessLimit 拦截器中对该注解进行解析，利用计数器技术法进行限流
        // 校验验证码
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check){
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        // 生成秒杀地址
        String str = orderService.createPath(user, goodsId);
        return RespBean.success(str);
    }

    /***
     * redis缓存库存，消息队列异步下单优化前 qps 69.1/sec
     * 优化后 1074/sec
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSeckill")
    @ResponseBody
    public RespBean doSecKill(User user, Long goodsId){
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //判断库存 redis内存标记，减少于redis交互
        if(emptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 判断是否重复抢购
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillGoods != null){
            return RespBean.error(RespBeanEnum.REAP_ORDER);
        }

        //预减库存, 用Lua脚本，保证其原子性
        Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
        if(stock < 0){
            emptyStockMap.put(goodsId, true);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 请求入队，立即返回排队中
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendsecKillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);
    }

    /**
     * 获取秒杀结果
     *
     * @param user
     * @param goodsId
     * @return orderId:成功，-1：秒杀失败，0：排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    /***
     * 初始化时将库存缓存到redis中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.findGoodsVoList();
        if (CollectionUtils.isEmpty(goodsVoList)){
            return;
        }
        goodsVoList.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(), false);
        });
    }
}
