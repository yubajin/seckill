package cn.yubajin.seckill.vo;

import cn.yubajin.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVO {
    private User user;
    private GoodsVo goodsVo;
    private int secKillStatus;
    private int remainSeconds;
}
