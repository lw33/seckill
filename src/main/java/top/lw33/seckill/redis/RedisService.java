package top.lw33.seckill.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import top.lw33.seckill.utils.JsonUtil;

/**
 * @Author lw
 * @Date 2019-01-15 22:22:24
 **/
@Service
public class RedisService {

    @Autowired
    private JedisPool jedisPool;

    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            String realKey = prefix + key;
            String res = jedis.get(realKey);
            T t = JsonUtil.string2bean(res, clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    public <T> boolean set(KeyPrefix prefix, String key, T t) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String value = JsonUtil.bean2string(t);
            if (StringUtils.isEmpty(value)) {
                return false;
            }
            String realKey = prefix.getPrefix() + prefix;
            if (prefix.expireSeconds() > 0) {
                jedis.setex(realKey, prefix.expireSeconds(), value);
            } else {
                jedis.set(realKey, value);
            }
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
