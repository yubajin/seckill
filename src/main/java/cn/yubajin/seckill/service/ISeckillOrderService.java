package cn.yubajin.seckill.service;

import cn.yubajin.seckill.pojo.SeckillOrder;
import cn.yubajin.seckill.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yubj17
 * @since 2021-10-31
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    Long getResult(User user, Long goodsId);
}
