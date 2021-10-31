package cn.yubajin.seckill.service;

import cn.yubajin.seckill.pojo.Goods;
import cn.yubajin.seckill.vo.GoodsVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yubj17
 * @since 2021-10-31
 */
public interface IGoodsService extends IService<Goods> {

    /***
     * 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVoList();

    /***
     * 获取商品详情
     * @param goodsId
     * @return
     */
    GoodsVo findGoodsVo(Long goodsId);
}
