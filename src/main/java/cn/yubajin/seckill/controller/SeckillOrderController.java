package cn.yubajin.seckill.controller;


import cn.yubajin.seckill.pojo.SeckillMessage;
import cn.yubajin.seckill.pojo.SeckillOrder;
import cn.yubajin.seckill.pojo.User;
import cn.yubajin.seckill.rabbitmq.MQSender;
import cn.yubajin.seckill.service.IGoodsService;
import cn.yubajin.seckill.service.IOrderService;
import cn.yubajin.seckill.service.ISeckillOrderService;
import cn.yubajin.seckill.utils.JsonUtil;
import cn.yubajin.seckill.vo.GoodsVo;
import cn.yubajin.seckill.vo.RespBean;
import cn.yubajin.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

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
     * redis缓存库存，消息队列异步下单优化前 qps 69.1/sec
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
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null){
            return RespBean.error(RespBeanEnum.REAP_ORDER);
        }
        // 预减库存
        Long stock = redisTemplate.opsForValue().decrement("seckillGoods:" + goodsId);
        if(stock < 0){
            emptyStockMap.put(goodsId, true);
            redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendsecKillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);
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
