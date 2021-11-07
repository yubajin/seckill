package cn.yubajin.seckill.rabbitmq;

import cn.yubajin.seckill.Exception.GlobalException;
import cn.yubajin.seckill.pojo.SeckillMessage;
import cn.yubajin.seckill.pojo.User;
import cn.yubajin.seckill.service.IGoodsService;
import cn.yubajin.seckill.service.IOrderService;
import cn.yubajin.seckill.utils.JsonUtil;
import cn.yubajin.seckill.vo.GoodsVo;
import cn.yubajin.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @since 1.0.0
 */
@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String msg) {
        log.info("QUEUE接受消息：" + msg);
        SeckillMessage message = JsonUtil.jsonStr2Object(msg, SeckillMessage.class);
        Long goodsId = message.getGoodsId();
        User user = message.getUser();
        //判断库存
        GoodsVo goods = goodsService.findGoodsVo(goodsId);
        if (goods.getStockCount() < 1) {
            throw new GlobalException(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢购
        String seckillOrderJson = (String)redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (!StringUtils.isEmpty(seckillOrderJson)) {
            throw new GlobalException(RespBeanEnum.REAP_ORDER);
        }
        orderService.seckill(user, goods);
    }
}
