package cn.yubajin.seckill.controller;


import cn.yubajin.seckill.pojo.Order;
import cn.yubajin.seckill.pojo.SeckillOrder;
import cn.yubajin.seckill.pojo.User;
import cn.yubajin.seckill.service.IGoodsService;
import cn.yubajin.seckill.service.IOrderService;
import cn.yubajin.seckill.service.ISeckillOrderService;
import cn.yubajin.seckill.vo.GoodsVo;
import cn.yubajin.seckill.vo.RespBeanEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
public class SeckillOrderController {
    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IOrderService orderService;

    @RequestMapping("/doSeckill")
    public String doSecKill(Model model, User user, Long goodsId){
        if (user == null){
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodsVo(goodsId);
        //判断库存
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "seckillFail";
        }
        // 判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(Wrappers.<SeckillOrder>query().lambda().
                eq(SeckillOrder::getGoodsId, goodsId).eq(SeckillOrder::getUserId, user.getId()));
        if (seckillOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REAP_ORDER.getMessage());
            return "seckillFail";
        }
        Order order = orderService.seckill(user, goods);
        model.addAttribute("order",order);
        model.addAttribute("goods",goods);
        return "orderDetail";
    }

}
