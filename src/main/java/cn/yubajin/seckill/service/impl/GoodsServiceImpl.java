package cn.yubajin.seckill.service.impl;

import cn.yubajin.seckill.mapper.GoodsMapper;
import cn.yubajin.seckill.pojo.Goods;
import cn.yubajin.seckill.service.IGoodsService;
import cn.yubajin.seckill.vo.GoodsVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yubj17
 * @since 2021-10-31
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public List<GoodsVo> findGoodsVoList() {
        return goodsMapper.findGoodsVoList();
    }

    @Override
    public GoodsVo findGoodsVo(Long goodsId) {
        return goodsMapper.findGoodsVoByGoodsId(goodsId);
    }
}
