package cn.yubajin.seckill.service.impl;

import cn.yubajin.seckill.Exception.GlobalException;
import cn.yubajin.seckill.mapper.UserMapper;
import cn.yubajin.seckill.pojo.User;
import cn.yubajin.seckill.service.IUserService;
import cn.yubajin.seckill.utils.CookieUtil;
import cn.yubajin.seckill.utils.JsonUtil;
import cn.yubajin.seckill.utils.SecurityUtils;
import cn.yubajin.seckill.utils.UUIDutil;
import cn.yubajin.seckill.vo.LoginVo;
import cn.yubajin.seckill.vo.RespBean;
import cn.yubajin.seckill.vo.RespBeanEnum;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yubj17
 * @since 2021-10-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean login(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        // 有了validator,此处不需要手动的参数校验判断
        //根据手机号获取用户
        User user = userMapper.selectById(mobile);
        if (null==user){
            throw new GlobalException(RespBeanEnum.LOGINVO_ERROR);
        }
        //校验密码
        if (!SecurityUtils.formPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
            throw new GlobalException(RespBeanEnum.LOGINVO_ERROR);
        }
        // 生成cookie
        String ticket = UUIDutil.uuid();
        redisTemplate.opsForValue().set("skuser:" + ticket, JsonUtil.object2JsonStr(user));
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }

    /**
     * 根据cookie获取用户
     * @param userTicket
     * @param request
     * @param response
     * @return
     */
    @Override
    public User getByUserTicket(String userTicket, HttpServletRequest request,
                                HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        String userJson = (String) redisTemplate.opsForValue().get("skuser:" +
                userTicket);
        User user = JsonUtil.jsonStr2Object(userJson, User.class);
        if (null != user) {
            CookieUtil.setCookie(request,response,"userTicket",userTicket);
        }
        return user;
    }

    /***
     * 更新密码
     * @param userTicket
     * @param password
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean updatePassWord(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = this.getByUserTicket(userTicket, request, response);
        if (user == null){
            throw new GlobalException(RespBeanEnum.EMPTY_STOCK);
        }
        user.setPassword(SecurityUtils.formPassToDBPass(password, user.getSalt()));
        int res = userMapper.updateById(user);
        if (res == 1){
            redisTemplate.delete("skuser:" + userTicket);
            return RespBean.success();
        }

        return RespBean.error(RespBeanEnum.UPDATE_PASSWORD_ERROR);
    }
}

