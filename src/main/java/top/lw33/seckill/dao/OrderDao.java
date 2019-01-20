package top.lw33.seckill.dao;

import org.apache.ibatis.annotations.*;
import top.lw33.seckill.entity.OrderInfo;
import top.lw33.seckill.entity.SeckillOrder;

/**
 * @Author lw
 * @Date 2019-01-17 20:44:30
 **/
@Mapper
public interface OrderDao {

    @Select("select * from seckill_order where user_id=#{userId} and goods_id=#{goodsId}")
    OrderInfo selectSeckillOrderByUserIdAndGoodsId(Long userId, Long goodsId);

    @Insert("insert into order_info" +
            "(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)" +
            "values(#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}," +
            " #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    long insertOrder(OrderInfo orderInfo);

    @Insert("insert into seckill_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    void insertSeckillOrder(SeckillOrder seckillOrder);

    @Select("select * from order_info where id=#{orderId}")
    OrderInfo selectOrderById(long orderId);

    @Delete("delete from  order_info")
    void deleteOrder();

    @Delete("delete from seckill_order")
    void deleteSeckillOrder();
}
