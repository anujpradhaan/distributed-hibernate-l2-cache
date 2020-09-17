package upgrad.hibernate.cache.jedis;

import org.hibernate.cache.CacheException;

public interface JedisCache {

	String getRegionName();

	boolean exists(String key);

	Object get(Object key) throws CacheException;

	void put(Object key, Object value) throws CacheException;

	void remove(Object key) throws CacheException;

	void destroy();

    boolean lock(Object key, Integer expireMsecs) throws InterruptedException;

    void unlock(Object key);
}
