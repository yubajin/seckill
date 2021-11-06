package cn.yubajin.seckill.service;

import cn.yubajin.seckill.pojo.Order;
import cn.yubajin.seckill.pojo.User;
import cn.yubajin.seckill.vo.GoodsVo;
import cn.yubajin.seckill.vo.OrderDetailVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yubj17
 * @since 2021-10-31
 */
public interface IOrderService extends IService<Order> {
    /**
     * 订单详情
     * @param orderId
     * @return
     */
    OrderDetailVo detail(Long orderId);

    /***
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    Order seckill(User user, GoodsVo goods);
}
