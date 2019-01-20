package top.lw33.seckill.redis;

/**
 * @Author lw
 * @Date 2019-01-16 10:35:09
 **/
public class OrderKeyPrefix extends BaseKeyPrefix{


    private OrderKeyPrefix(String prefix) {
        super(prefix);
    }

    public static final OrderKeyPrefix orderGoodIdUserId = new OrderKeyPrefix("orderGoodIdUserId");
}
