package top.lw33.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.lw33.seckill.dto.CodeMsg;
import top.lw33.seckill.entity.OrderInfo;
import top.lw33.seckill.entity.User;
import top.lw33.seckill.service.GoodsService;
import top.lw33.seckill.service.OrderService;
import top.lw33.seckill.service.SeckillService;
import top.lw33.seckill.vo.GoodsVo;

/**
 * @Author lw
 * @Date 2019-01-17 20:39:16
 **/
@Controller
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;


    @RequestMapping("/do_seckill")
    public String doSeckill(Model model, User user, @RequestParam("goodsId")Long goodsId) {
        if (user == null) {
            return "login";
        }

        GoodsVo goods= goodsService.getGoodsVoById(goodsId);
        int stockCount = goods.getStockCount();
        // 判断库存
        if (stockCount <= 0) {
            model.addAttribute("errmsg", CodeMsg.SECKILL_OVER.getMsg());
            return "seckill_fail";
        }

        // 判断是否已秒过
        OrderInfo order = orderService.getSeckillOrderByUserIdAndGoodsId(user.getId(), goodsId);

        if (order != null) {
            model.addAttribute("errmsg", CodeMsg.REPEATE_SECKILL.getMsg());
            return "seckill_fail";
        }

        // 开始秒杀
        order = seckillService.seckill(user, goods);
        model.addAttribute("goods", goods);
        model.addAttribute("orderInfo", order);
        return "order_detail";
    }
}
