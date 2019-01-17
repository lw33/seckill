package top.lw33.seckill.redis;

/**
 * @Author lw
 * @Date 2019-01-16 10:35:09
 **/
public class UserKeyPrefix extends BaseKeyPrefix{

    private UserKeyPrefix(String prefix) {
        super(prefix);
    }

    public static final UserKeyPrefix idKey = new UserKeyPrefix("id");
    public static final UserKeyPrefix nameKey = new UserKeyPrefix("name");


}
