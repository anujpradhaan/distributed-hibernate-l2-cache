package upgrad.hibernate.cache.region;

import upgrad.hibernate.cache.jedis.JedisCache;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.Region;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author : anuj.kumar
 **/
@Slf4j
public abstract class JedisRegion implements Region {

	private static final String CACHE_LOCK_TIMEOUT_PROPERTY = "net.daum.clix.hibernate.redis.cache_lock_timeout";
	private static final int DEFAULT_CACHE_LOCK_TIMEOUT = 1000;

	protected final JedisCache cache;

	private int cacheLockTimeout;

	protected JedisRegion(JedisCache cache, Properties properties) {
		this.cache = cache;
		String timeoutProperty = properties.getProperty(CACHE_LOCK_TIMEOUT_PROPERTY);
		this.cacheLockTimeout = timeoutProperty == null ? DEFAULT_CACHE_LOCK_TIMEOUT : Integer.parseInt(timeoutProperty);
	}

	@Override
	public String getName() {
		return cache.getRegionName();
	}

	@Override
	public void destroy() throws CacheException {
		//cache.destroy();
	}

	@Override
	public long getSizeInMemory() {
		return -1;
	}

	@Override
	public long getElementCountInMemory() {
		return -1;
	}

	@Override
	public long getElementCountOnDisk() {
		return -1;
	}

	@Override
	public Map toMap() {
		return new HashMap();
	}

	@Override
	public long nextTimestamp() {
		return System.currentTimeMillis() / 100;
	}

	@Override
	public int getTimeout() {
		return cacheLockTimeout;
	}

	@Override
	public boolean contains(Object key) {
		long startTime = System.currentTimeMillis();
		boolean contains = containsWithTime(key);
		long endTime = System.currentTimeMillis();
		log.debug("Time elapsed to check of key exists in redis={} ms", endTime - startTime);
		return contains;
	}

	private boolean containsWithTime(Object key) {
		log.debug("Checking if key exists:{}", key.toString());
		return cache.exists(key.toString());
	}

	public Object get(Object key) {
		long startTime = System.currentTimeMillis();
		Object value = getWithTime(key);
		long endTime = System.currentTimeMillis();
		log.debug("Time elapsed to get value from redis={} ms", endTime - startTime);
		return value;
	}

	private Object getWithTime(Object key) {
		log.debug("Getting value for key:{}", key.toString());
		return cache.get(key);
	}

	public void put(Object key, Object value) {
		long startTime = System.currentTimeMillis();
		putWithTime(key, value);
		long endTime = System.currentTimeMillis();
		log.debug("Time elapsed to update in redis={} ms", endTime - startTime);
	}

	private void putWithTime(Object key, Object value) {
		log.debug("Updating value for key:{} with value {}", key.toString(), value.toString());
		cache.put(key, value);
	}

	public JedisCache getRedisCache() {
		log.debug("Getting Redis Cache");
		return cache;
	}

	public boolean writeLock(Object key) {
		try {
			boolean lockAcquired = false;
			long startTime = System.currentTimeMillis();
			lockAcquired = cache.lock(key, getTimeout());
			long endTime = System.currentTimeMillis();
			log.debug("Time elapsed to lock in redis={} ms", endTime - startTime);
			return lockAcquired;
		} catch (InterruptedException e) {
			return false;
		}
	}

	public void releaseLock(Object key) {
		long startTime = System.currentTimeMillis();
		cache.unlock(key);
		long endTime = System.currentTimeMillis();
		log.debug("Time elapsed to unlock in redis={} ms", endTime - startTime);
	}
}
