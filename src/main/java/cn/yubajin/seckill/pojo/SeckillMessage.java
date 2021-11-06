package cn.yubajin.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessage {
    private User user;
    private Long goodsId;
}