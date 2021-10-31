package cn.yubajin.seckill.service;

import cn.yubajin.seckill.vo.RespBean;
import cn.yubajin.seckill.vo.LoginVo;
import cn.yubajin.seckill.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yubj17
 * @since 2021-10-30
 */
public interface IUserService extends IService<User> {

    /**
     * 登录
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    RespBean login(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据cookie获取用户
     * @param userTicket
     * @param request
     * @param response
     * @return
     */
    User getByUserTicket(String userTicket,HttpServletRequest request, HttpServletResponse response);
}
