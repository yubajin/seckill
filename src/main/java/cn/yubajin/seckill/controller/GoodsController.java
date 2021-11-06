package cn.yubajin.seckill.controller;

import cn.yubajin.seckill.pojo.User;
import cn.yubajin.seckill.service.IGoodsService;
import cn.yubajin.seckill.vo.DetailVO;
import cn.yubajin.seckill.vo.GoodsVo;
import cn.yubajin.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /***
     * 调转登录页面
     * @param
     * @param model
     * @return
     */
    /***
     * user 传入controller前被UserArgumentResolver解析
     * thymeleaf页面缓存
     */
    @RequestMapping(value = "toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response,
                         Model model, User user){
        if (user == null){
            return "login";
        }
        String html = (String) redisTemplate.opsForValue().get("goodsList");
        if (html != null){
            return html;
        }
        List<GoodsVo> goodsVos = goodsService.findGoodsVoList();
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsVos);

        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (html != null){
            redisTemplate.opsForValue().set("goodsList", html, 30, TimeUnit.SECONDS);
        }
        return html;
    }

    @RequestMapping("toDetail/{goodsId}")
    public String toDetail2(Model model, User user, @PathVariable("goodsId") Long goodsId){
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


    @RequestMapping("detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(User user, @PathVariable("goodsId") Long goodsId){
        GoodsVo goodsVo = goodsService.findGoodsVo(goodsId);
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
        DetailVO detailVO = new DetailVO();
        detailVO.setGoodsVo(goodsVo);
        detailVO.setUser(user);
        detailVO.setRemainSeconds(remainSeconds);
        detailVO.setSecKillStatus(secKillStatus);
        return RespBean.success(detailVO);
    }
}
