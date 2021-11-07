package cn.yubajin.seckill.common.cache;

import cn.yubajin.seckill.pojo.SeckillOrder;
import cn.yubajin.seckill.pojo.User;
import cn.yubajin.seckill.service.IUserService;
import cn.yubajin.seckill.utils.CacheUtil;
import cn.yubajin.seckill.utils.SpringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CommonCache {

    private static  final IUserService userService;

    static {
        userService = SpringUtils.getBean(IUserService.class);
    }

    public static Integer getStock(){

        return null;
    }

    public static String getGoodsListHtml(){

        return null;
    }

    public static User getUser(String ticket){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes == null ? null : ((ServletRequestAttributes)requestAttributes).getRequest();
        HttpServletResponse response = requestAttributes == null ? null : ((ServletRequestAttributes)requestAttributes).getResponse();
        return CacheUtil.get(CacheName.USER, "", ticket, () ->{ return userService.getByUserTicket(ticket, request, response);});
    }

    public  static SeckillOrder getSeckillOrder(){

        return null;
    }
}
