package top.lw33.seckill.utils;

import java.util.UUID;

/**
 * @Author lw
 * @Date 2019-01-16 12:11:13
 **/
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
