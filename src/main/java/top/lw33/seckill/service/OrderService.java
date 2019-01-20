package top.lw33.seckill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.lw33.seckill.dao.OrderDao;
import top.lw33.seckill.entity.OrderInfo;
import top.lw33.seckill.entity.SeckillOrder;
import top.lw33.seckill.entity.User;
import top.lw33.seckill.redis.OrderKeyPrefix;
import top.lw33.seckill.redis.RedisService;
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

    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsService goodsService;

    public OrderInfo getSeckillOrderByUserIdAndGoodsId(Long userId, Long goodsId) {
        //return orderDao.selectSeckillOrderByUserIdAndGoodsId(userId, goodsId);
        return redisService.get(OrderKeyPrefix.orderGoodIdUserId, userId + ":" + goodsId, OrderInfo.class);
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.selectOrderById(orderId);
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

        // 将订单加入缓存
        redisService.set(OrderKeyPrefix.orderGoodIdUserId, user.getId() + ":" + goods.getId(), orderInfo);
        return orderInfo;
    }

    @Transactional
    public OrderInfo createOrder(long userId, GoodsVo goods ) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setOrderChannel(1);
        orderInfo.setUserId(userId);
        orderInfo.setStatus(0);
        long orderId = orderDao.insertOrder(orderInfo);
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderId);
        seckillOrder.setUserId(userId);
        orderDao.insertSeckillOrder(seckillOrder);

        // 将订单加入缓存
        redisService.set(OrderKeyPrefix.orderGoodIdUserId, userId+ ":" + goods.getId(), orderInfo);
        return orderInfo;
    }

    public void deleteOrders() {
        orderDao.deleteOrder();
        orderDao.deleteSeckillOrder();
    }
}
