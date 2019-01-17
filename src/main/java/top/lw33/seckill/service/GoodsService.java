package top.lw33.seckill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.lw33.seckill.dao.GoodsDao;
import top.lw33.seckill.vo.GoodsVo;

import java.util.List;

/**
 * @Author lw
 * @Date 2019-01-17 19:50:12
 **/
@Service
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    public List<GoodsVo> getGoodsVoList() {
        return goodsDao.selectGoodsVoList();
    }

    public GoodsVo getGoodsVoById(Long goodsId) {
        return goodsDao.selectGoodsVoById(goodsId);
    }

    public void reduceStock(GoodsVo goods) {
        goodsDao.updateStock(goods.getId());
    }
}
