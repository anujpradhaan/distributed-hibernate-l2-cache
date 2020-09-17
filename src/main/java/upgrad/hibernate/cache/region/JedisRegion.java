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

	/**
	 * RedisCache instance backing this Hibernate data region.
	 */
	protected final JedisCache cache;

	private int cacheLockTimeout;

	/**
	 * Create a Hibernate data region backed by the given Redis instance.
	 */
	protected JedisRegion(JedisCache cache, Properties properties) {
		this.cache = cache;
		String timeoutProperty = properties.getProperty(CACHE_LOCK_TIMEOUT_PROPERTY);
		this.cacheLockTimeout = timeoutProperty == null ? DEFAULT_CACHE_LOCK_TIMEOUT : Integer.parseInt(timeoutProperty);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return cache.getRegionName();
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroy() throws CacheException {
		cache.destroy();
	}

	/**
	 * {@inheritDoc}
	 */
	public long getSizeInMemory() {
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getElementCountInMemory() {
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getElementCountOnDisk() {
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map toMap() {
		return new HashMap();
	}

	/**
	 * {@inheritDoc}
	 */
	public long nextTimestamp() {
		return System.currentTimeMillis() / 100;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getTimeout() {
		return cacheLockTimeout;
	}

	/**
	 * Return the RedisCache instance backing this Hibernate data region.
	 */
	public JedisCache getRedisCache() {
		return cache;
	}

	/**
	 * Returns <code>true</code> if this region contains data for the given key.
	 * <p/>
	 * This is a Hibernate 3.5 method.
	 */
	public boolean contains(Object key) {
		return cache.exists(key.toString());
	}

    public Object get(Object key){
        return cache.get(key);
    }

    public void put(Object key, Object value){
        cache.put(key, value);
    }

    public boolean writeLock(Object key){
        try {
            return cache.lock(key, getTimeout());
        } catch (InterruptedException e) {
            return false;
        }
    }

    public void releaseLock(Object key){
        cache.unlock(key);
    }
}
