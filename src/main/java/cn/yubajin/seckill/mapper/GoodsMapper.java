package cn.yubajin.seckill.mapper;

import cn.yubajin.seckill.pojo.Goods;
import cn.yubajin.seckill.vo.GoodsVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yubj17
 * @since 2021-10-31
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /***
     * 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVoList();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
