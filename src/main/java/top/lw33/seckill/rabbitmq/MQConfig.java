package top.lw33.seckill.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author lw
 * @Date 2019-01-18 21:00:29
 **/
@Configuration
public class MQConfig {


    public static final String QUEUE = "queue";
    public static final String SECKILL_QUEUE = "seckill.queue";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Queue seckillQueue() {
        return new Queue(SECKILL_QUEUE, true);
    }
}
