package top.lw33.seckill.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import top.lw33.seckill.vo.GoodsVo;

import java.util.List;

/**
 * @Author lw
 * @Date 2019-01-17 19:54:02
 **/
@Mapper
public interface GoodsDao {

    @Select("select g.*, sg.stock_count, sg.start_date, sg.end_date, sg.seckill_price" +
            " from goods g, seckill_goods sg where g.id=sg.goods_id")
    List<GoodsVo> selectGoodsVoList();

    @Select("select g.*, sg.stock_count, sg.start_date, sg.end_date, sg.seckill_price" +
            " from goods g, seckill_goods sg where g.id=sg.goods_id and goods_id = #{goodsId}")
    GoodsVo selectGoodsVoById(Long goodsId);

    @Update("update seckill_goods set stock_count=stock_count-1 where goods_id=#{id}")
    void updateStock(Long id);
}
