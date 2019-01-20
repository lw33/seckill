package top.lw33.seckill.vo;

import top.lw33.seckill.entity.OrderInfo;

/**
 * @Author lw
 * @Date 2019-01-20 11:34:55
 **/
public class OrderVo {

    private GoodsVo goods;
    private OrderInfo order;

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public OrderInfo getOrder() {
        return order;
    }

    public void setOrder(OrderInfo order) {
        this.order = order;
    }
}
