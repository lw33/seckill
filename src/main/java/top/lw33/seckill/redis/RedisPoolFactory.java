package top.lw33.seckill.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author lw
 * @Date 2019-01-15 19:45:54
 **/
@Configuration
public class RedisPoolFactory {

    @Bean
    public JedisPool jedisPool(RedisConfig redisConfig) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        jedisPoolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        jedisPoolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000);
        return new JedisPool(jedisPoolConfig, redisConfig.getHost(), redisConfig.getPort(),
                redisConfig.getTimeout(), redisConfig.getPassword(), redisConfig.getDatabase());
    }
}
