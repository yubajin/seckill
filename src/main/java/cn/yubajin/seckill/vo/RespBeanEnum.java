package cn.yubajin.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 返回状态枚举
 *
 * @author zhoubin
 * @since 1.0.0
 */
@ToString
@Getter
@AllArgsConstructor
public enum RespBeanEnum {
    //通用状态码
    SUCCESS(200,"success"),
    ERROR(500,"服务端异常"),
    //登录模块5002xx
    SESSION_ERROR(500210,"session不存在或者已经失效"),
    LOGINVO_ERROR(500211,"用户名或者密码错误"),
    MOBILE_ERROR(500212,"手机号码格式错误"),
    BIND_ERROR(500213, "提示消息绑定错误"),
    //秒杀模块5005xx
    EMPTY_STOCK(500510, "商品库存不足"),
    REAP_ORDER(500511, "该商品每人限购一件"),
    //用户模块5006xx
    USER_ERROR(500610, "用户不存在"),
    UPDATE_PASSWORD_ERROR(500611, "密码更新失败"),
    //订单模块
    ORDER_NOT_EXIST(500710, "订单不存在");

    private final Integer code;
    private final String message;
}