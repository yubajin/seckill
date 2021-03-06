package cn.yubajin.seckill.controller;


import cn.yubajin.seckill.pojo.User;
import cn.yubajin.seckill.vo.RespBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yubj17
 * @since 2021-10-30
 */
@Controller
@RequestMapping("/user")
public class UserController {

    /**
     * 用户信息(测试)
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }
}
