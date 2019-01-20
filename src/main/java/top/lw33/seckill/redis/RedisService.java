package top.lw33.seckill.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import top.lw33.seckill.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @Author lw
 * @Date 2019-01-15 22:22:24
 **/
@Service
public class RedisService {

    @Autowired
    private JedisPool jedisPool;

    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {

        return template(prefix, key, (j, k) -> JsonUtil.string2bean(j.get(k), clazz));
    }

    public <T> boolean set(KeyPrefix prefix, String key, T t) {

        return template(prefix, key, (j, k) -> {
            String value = JsonUtil.bean2string(t);
            if (StringUtils.isEmpty(value)) {
                return false;
            }
            if (prefix.expireSeconds() > 0) {
                j.setex(k, prefix.expireSeconds(), value);
            } else {
                j.set(k, value);
            }
            return true;
        });
    }

    public boolean delete(KeyPrefix prefix, String key) {

        return template(prefix, key, (j, k) -> j.del(k) > 0);
    }

    public long incr(KeyPrefix prefix, String key) {

        return template(prefix, key, Jedis::incr);
    }

    public long decr(KeyPrefix prefix, String key) {

        return template(prefix, key, Jedis::decr);
    }

    public boolean exists(KeyPrefix prefix, String key) {

        return template(prefix, key, Jedis::exists);
    }

    /**
     * 删除 prefix 开头的所有的键
     * @param prefix
     * @return
     */
    public boolean delete(KeyPrefix prefix) {
        if(prefix == null) {
            return false;
        }
        List<String> keys = scanKeys(prefix.getPrefix());
        if(keys==null || keys.size() <= 0) {
            return true;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(keys.toArray(new String[0]));
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }
    public List<String> scanKeys(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<String> keys = new ArrayList<>();
            String cursor = "0";
            ScanParams sp = new ScanParams();
            sp.match("*"+key+"*");
            sp.count(100);
            do{
                ScanResult<String> ret = jedis.scan(cursor, sp);
                List<String> result = ret.getResult();
                if(result!=null && result.size() > 0){
                    keys.addAll(result);
                }
                //再处理cursor
                cursor = ret.getStringCursor();
            }while(!cursor.equals("0"));
            return keys;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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
