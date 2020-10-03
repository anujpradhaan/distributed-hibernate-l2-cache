package upgrad.hibernate.cache.jedis;

import org.hibernate.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import upgrad.hibernate.cache.serializer.RedisSerializer;
import upgrad.hibernate.cache.serializer.SnappyRedisSerializer;
import upgrad.hibernate.cache.serializer.StringRedisSerializer;

public class JedisCacheImpl implements JedisCache {

	private JedisPool writeJedisPool;

	private JedisPool readJedisPool;

	private String regionName;

	private final StringRedisSerializer keySerializer = new StringRedisSerializer();
	private final RedisSerializer<Object> valueSerializer = new SnappyRedisSerializer<>();

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public JedisCacheImpl(JedisPool writeJedisPool, JedisPool readJedisPool, String regionName) {
		this.writeJedisPool = writeJedisPool;
		this.readJedisPool = readJedisPool;
		this.regionName = regionName;
	}

	@Override
	public Object get(Object key) throws CacheException {
		Object o = null;
		byte[] k = serializeObject(key.toString());
		Jedis jedis = readJedisPool.getResource();
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
		Jedis jedis = writeJedisPool.getResource();
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
		Jedis jedis = writeJedisPool.getResource();
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
		Jedis jedis = readJedisPool.getResource();
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

	private byte[] serializeKey(String key) {
		return keySerializer.serialize(key);
	}

	private Object deserializeKey(byte[] b) {
		return keySerializer.deserialize(b);
	}

	private byte[] serializeObject(Object obj) {
		return valueSerializer.serialize(obj);
	}

	private Object deserializeObject(byte[] b) {
		return valueSerializer.deserialize(b);
	}

	/**
	 * we’ll attempt to acquire the lock by using SETNX to set the value of the
	 * lock’s key only if it doesn’t already exist. On failure, we’ll continue to attempt this
	 * until we’ve run out of time (which defaults to 10 seconds).
	 *
	 * @param key
	 * @param expireMsecs
	 * @return
	 * @throws InterruptedException
	 */
	public boolean lock(Object key, Integer expireMsecs) throws InterruptedException {
		String lockKey = generateLockKey(key);
		long expires = System.currentTimeMillis() + expireMsecs + 1;
		String expiresStr = String.valueOf(expires);
		long timeout = expireMsecs;
		while (timeout >= 0) {
			Jedis jedis = writeJedisPool.getResource();

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
		Jedis jedis = writeJedisPool.getResource();
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
