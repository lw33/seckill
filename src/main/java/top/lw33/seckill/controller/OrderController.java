package top.lw33.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.lw33.seckill.dto.CodeMsg;
import top.lw33.seckill.dto.Result;
import top.lw33.seckill.entity.OrderInfo;
import top.lw33.seckill.entity.User;
import top.lw33.seckill.redis.RedisService;
import top.lw33.seckill.service.GoodsService;
import top.lw33.seckill.service.OrderService;
import top.lw33.seckill.service.UserService;
import top.lw33.seckill.vo.GoodsVo;
import top.lw33.seckill.vo.OrderVo;

/**
 * @Author lw
 * @Date 2019-01-20 11:33:47
 **/
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;


    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public Result<OrderVo> detail(User user, @RequestParam("orderId") long orderId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if (order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        GoodsVo goods = goodsService.getGoodsVoById(order.getGoodsId());
        OrderVo orderVo = new OrderVo();
        orderVo.setOrder(order);
        orderVo.setGoods(goods);
        return Result.success(orderVo);
    }

}
