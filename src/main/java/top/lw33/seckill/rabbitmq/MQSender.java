package top.lw33.seckill.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.lw33.seckill.utils.JsonUtil;

/**
 * @Author lw
 * @Date 2019-01-18 20:59:36
 **/
@Service
public class MQSender {


    private static final Logger logger = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send(Object message) {
        String msg = JsonUtil.bean2string(message);
        logger.info("sender "+msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
    }

    public void sendSeckillMessage(SeckillMessage seckillMessage) {
        String msg = JsonUtil.bean2string(seckillMessage);
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, msg);
    }
}
