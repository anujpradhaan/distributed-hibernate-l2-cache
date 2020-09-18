package upgrad.hibernate.cache.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.time.Duration;
import java.util.Properties;

/**
 * @author : anuj.kumar
 **/
public class JedisConnectionFactory {

	public static JedisPool getJedisPool(Properties properties) {
		GenericObjectPoolConfig jedisPoolConfig = new GenericObjectPoolConfig();
		jedisPoolConfig.setMaxTotal(100);
		jedisPoolConfig.setMaxIdle(100);
		jedisPoolConfig.setMinIdle(50);
		jedisPoolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
		jedisPoolConfig.setNumTestsPerEvictionRun(3);
		jedisPoolConfig.setBlockWhenExhausted(true);
		return new JedisPool(jedisPoolConfig, properties.getProperty("redis.host", "localhost"),
				Integer.valueOf(properties.getProperty("redis.port", String.valueOf(Protocol.DEFAULT_PORT))),
				Integer.valueOf(properties.getProperty("redis.timeout", String.valueOf(Protocol.DEFAULT_TIMEOUT))),
				properties.getProperty("redis.password", null));
	}
}
