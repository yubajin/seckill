package cn.yubajin.seckill.controller;

import cn.yubajin.seckill.pojo.User;
import cn.yubajin.seckill.service.IGoodsService;
import cn.yubajin.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;

    /***
     * 调转登录页面
     * @param
     * @param model
     * @return
     */
    @RequestMapping("toList")
    /***
     * user 传入controller前被UserArgumentResolver解析
     */
    public String toList(Model model, User user){
//        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
//        if(userTicket == null){
//            return "login";
//        }
//        User user = userService.getByUserTicket(userTicket, request, response);
//        if (user == null){
//            return "login";
//        }
        if (user == null){
            return "login";
        }
        List<GoodsVo> goodsVos = goodsService.findGoodsVoList();
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsVos);
        return "goodsList";
    }

    @RequestMapping("toDetail/{goodsId}")
    public String toDetail(Model model, User user, @PathVariable("goodsId") Long goodsId){
        if (user == null){
            return "login";
        }
        GoodsVo goodsVo = goodsService.findGoodsVo(goodsId);
        model.addAttribute("user", user);
        model.addAttribute("goods", goodsVo);

        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int remainSeconds = 0;
        int secKillStatus = 0;
        // 秒杀前
        if (nowDate.before(startDate)){
            remainSeconds = ((int) ((startDate.getTime() - nowDate.getTime()) / 1000));
        // 秒杀后
        } else if(nowDate.after(endDate)){
            remainSeconds = -1;
            secKillStatus = 2;
        // 秒杀中
        }else {
            remainSeconds = 0;
            secKillStatus = 1;
        }
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("secKillStatus", secKillStatus);

        return "goodsDetail";
    }
}
