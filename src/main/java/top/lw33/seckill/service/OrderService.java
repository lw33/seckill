package top.lw33.seckill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.lw33.seckill.dao.OrderDao;
import top.lw33.seckill.entity.OrderInfo;
import top.lw33.seckill.entity.SeckillOrder;
import top.lw33.seckill.entity.User;
import top.lw33.seckill.vo.GoodsVo;

import java.util.Date;

/**
 * @Author lw
 * @Date 2019-01-17 20:44:09
 **/
@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    public OrderInfo getSeckillOrderByUserIdAndGoodsId(Long userId, Long goodsId) {
        return orderDao.selectSeckillOrderByUserIdAndGoodsId(userId, goodsId);
    }

    @Transactional
    public OrderInfo createOrder(User user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        long orderId = orderDao.insertOrder(orderInfo);
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderId);
        seckillOrder.setUserId(user.getId());
        orderDao.insertSeckillOrder(seckillOrder);
        return orderInfo;
    }
}
