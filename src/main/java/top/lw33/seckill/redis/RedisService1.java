package top.lw33.seckill.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import top.lw33.seckill.utils.JsonUtil;

import java.util.function.BiFunction;

/**
 * @Author lw
 * @Date 2019-01-15 22:22:24
 **/
//@Service
public class RedisService1 {

    @Autowired
    private JedisPool jedisPool;

    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
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
            String realKey = prefix.getPrefix() + key;
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

    public boolean delete(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            long del = jedis.del(realKey);
            return del > 0;
        } finally {
            returnToPool(jedis);
        }
    }

    public long incr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }


    public long decr(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    public boolean exists(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    public <R> R template(KeyPrefix prefix, String key, BiFunction<Jedis, String, R> function) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return function.apply(jedis, realKey);
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
