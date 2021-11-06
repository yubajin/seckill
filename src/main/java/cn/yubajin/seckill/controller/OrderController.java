package cn.yubajin.seckill.controller;


import cn.yubajin.seckill.pojo.User;
import cn.yubajin.seckill.service.IOrderService;
import cn.yubajin.seckill.vo.OrderDetailVo;
import cn.yubajin.seckill.vo.RespBean;
import cn.yubajin.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yubj17
 * @since 2021-10-31
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IOrderService orderService;
    /**
     * 订单详情
     * @param user
     * @param orderId
     * @return
     */
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId){
        if (null==user){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo detail = orderService.detail(orderId);
        return RespBean.success(detail);
    }
}
