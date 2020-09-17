package upgrad.hibernate.cache.region.timestamp;

import upgrad.hibernate.cache.jedis.JedisCache;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.TimestampsRegion;
import upgrad.hibernate.cache.region.JedisRegion;

import java.util.Properties;

/**
 * @author : anuj.kumar
 **/
@Slf4j
public class JedisTimestampsRegion extends JedisRegion implements TimestampsRegion {

	public JedisTimestampsRegion(JedisCache cache, Properties properties) {
		super(cache, properties);
	}

	@Override
	public Object get(Object key) throws CacheException {
		log.debug("Called get for key : {}", key);
		return cache.get(key);
	}

	@Override
	public void put(Object key, Object value) throws CacheException {
		log.debug("Called put by Key:{}, Value:{}", key, value);
		cache.put(key, value);
	}

	@Override
	public void evict(Object key) throws CacheException {
		cache.remove(key);
	}

	@Override
	public void evictAll() throws CacheException {
		throw new UnsupportedOperationException("Not Supported!!!");
	}
}
