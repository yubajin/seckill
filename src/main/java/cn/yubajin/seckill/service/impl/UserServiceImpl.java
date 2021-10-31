package cn.yubajin.seckill.service.impl;

import cn.yubajin.seckill.Exception.GlobalException;
import cn.yubajin.seckill.vo.RespBean;
import cn.yubajin.seckill.vo.RespBeanEnum;
import cn.yubajin.seckill.mapper.UserMapper;
import cn.yubajin.seckill.vo.LoginVo;
import cn.yubajin.seckill.pojo.User;
import cn.yubajin.seckill.service.IUserService;
import cn.yubajin.seckill.utils.CookieUtil;
import cn.yubajin.seckill.utils.JsonUtil;
import cn.yubajin.seckill.utils.SecurityUtils;
import cn.yubajin.seckill.utils.UUIDutil;
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
        // 有了validator,不需要此处的参数校验判断
//        if (StringUtils.isEmpty(mobile)|| StringUtils.isEmpty(password)){
//            return RespBean.error(RespBeanEnum.LOGINVO_ERROR);
//        }
//        if (!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }
        //根据手机号获取用户
        User user = userMapper.selectById(mobile);
        if (null==user){
//            return RespBean.error(RespBeanEnum.LOGINVO_ERROR);
            throw new GlobalException(RespBeanEnum.LOGINVO_ERROR);
        }
        //校验密码
        if (!SecurityUtils.formPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
//            return RespBean.error(RespBeanEnum.LOGINVO_ERROR);
            throw new GlobalException(RespBeanEnum.LOGINVO_ERROR);
        }
        // 生成cookie
        String ticket = UUIDutil.uuid();
        redisTemplate.opsForValue().set("skuser:" + ticket, JsonUtil.object2JsonStr(user));
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success();
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
}
