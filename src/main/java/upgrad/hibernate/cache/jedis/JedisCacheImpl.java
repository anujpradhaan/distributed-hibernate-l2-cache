package upgrad.hibernate.cache.jedis;

import org.hibernate.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisCacheImpl implements JedisCache {

	private JedisPool jedisPool;

	private String regionName;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public JedisCacheImpl(JedisPool jedisPool, String regionName) {
		this.jedisPool = jedisPool;
		this.regionName = regionName;
		//this.jedis = jedisPool.getResource();
	}

	@Override
	public Object get(Object key) throws CacheException {
		Object o = null;
		byte[] k = serializeObject(key.toString());
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] v = jedis.get(k);
			if (v != null && v.length > 0) {
				o = deserializeObject(v);
			}
			return o;
		} catch (JedisConnectionException e) {
			logger.error(key.toString(), e);
		} finally {
			jedis.close();
		}
		return null;
	}

	@Override
	public void put(Object key, Object value) throws CacheException {
		byte[] k = serializeObject(key.toString());
		byte[] v = serializeObject(value);
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.set(k, v);
		} catch (JedisConnectionException e) {
			logger.error(key.toString(), e);
		} finally {
			jedis.close();
		}
	}

	@Override
	public void remove(Object key) throws CacheException {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.del(serializeObject(key.toString()));
		} catch (JedisConnectionException e) {
			logger.error(key.toString(), e);
		} finally {
			jedis.close();
		}
	}

	@Override
	public boolean exists(String key) {
		boolean exists = false;
		Jedis jedis = jedisPool.getResource();
		try {
			exists = jedis.exists(serializeObject(key));
		} catch (JedisConnectionException e) {
			logger.error(key, e);
		} finally {
			jedis.close();
		}
		return exists;
	}

	@Override
	public String getRegionName() {
		return this.regionName;
	}

	private byte[] serializeObject(Object obj) {
		SerializingConverter sc = new SerializingConverter();
		return sc.convert(obj);
	}

	private Object deserializeObject(byte[] b) {
		DeserializingConverter dc = new DeserializingConverter();
		return dc.convert(b);
	}

	public boolean lock(Object key, Integer expireMsecs) throws InterruptedException {

		String lockKey = generateLockKey(key);
		long expires = System.currentTimeMillis() + expireMsecs + 1;
		String expiresStr = String.valueOf(expires);
		long timeout = expireMsecs;
		while (timeout >= 0) {
			Jedis jedis = jedisPool.getResource();
			try {
				if (jedis.setnx(lockKey, expiresStr) == 1) {
					return true;
				}

				String currentValueStr = jedis.get(lockKey);
				if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
					// lock is expired

					String oldValueStr = jedis.getSet(lockKey, expiresStr);
					if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
						// lock acquired
						return true;
					}
				}
			} catch (JedisConnectionException e) {
				logger.error("Error while locking", e);
				return false;
			} finally {
				jedis.close();
			}
			logger.info("{} is now locking and waiting for unlock", key.toString());
			timeout -= 100;
			Thread.sleep(100);
		}
		return false;
	}

	@Override
	public void unlock(Object key) {
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.del(generateLockKey(key));
		} finally {
			jedis.close();
		}
	}

	private String generateLockKey(Object key) {
		if (null == key) {
			throw new IllegalArgumentException("key must not be null");
		}
		return key.toString() + ".lock";
	}

}
