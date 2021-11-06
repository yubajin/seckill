package cn.yubajin.seckill.service.impl;

import cn.yubajin.seckill.mapper.SeckillOrderMapper;
import cn.yubajin.seckill.pojo.SeckillOrder;
import cn.yubajin.seckill.pojo.User;
import cn.yubajin.seckill.service.ISeckillOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yubj17
 * @since 2021-10-31
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 获取秒杀结果
     *
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(Wrappers.<SeckillOrder>query().lambda().
                eq(SeckillOrder::getUserId, user.getId()).eq(SeckillOrder::getGoodsId, goodsId));
        if (null != seckillOrder) {
            return seckillOrder.getId();
        } else {
            if (redisTemplate.hasKey("isStockEmpty:" + goodsId)) {
                return -1L;
            }else {
                return 0L;
            }
        }
    }
}