package cn.yubajin.seckill.vo;

import cn.yubajin.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVo {
    private Order order;
    private GoodsVo goodsVo;
}
