package top.lw33.seckill.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.lw33.seckill.entity.OrderInfo;
import top.lw33.seckill.service.GoodsService;
import top.lw33.seckill.service.OrderService;
import top.lw33.seckill.service.SeckillService;
import top.lw33.seckill.utils.JsonUtil;
import top.lw33.seckill.vo.GoodsVo;

/**
 * @Author lw
 * @Date 2019-01-18 21:00:18
 **/
@Service
public class MQReceiver {

    private static final Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String msg) {
        logger.info("receive " + msg);
    }

    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receiveSeckillMessage(String msg) {
        SeckillMessage seckillMessage = JsonUtil.string2bean(msg, SeckillMessage.class);
        if (seckillMessage != null) {
            GoodsVo goods = goodsService.getGoodsVoById(seckillMessage.getGoodsId());
            // 判断库存
            if (goods.getStockCount() <= 0) {
                return;
            }
            // 判断是否已秒过
            OrderInfo order = orderService.getSeckillOrderByUserIdAndGoodsId(seckillMessage.getUserId(), seckillMessage.getGoodsId());

            if (order != null) {
                return;
            }
            seckillService.seckill(seckillMessage.getUserId(), goods);
        }
    }
}
