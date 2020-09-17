package upgrad.hibernate.cache.region.query;

import upgrad.hibernate.cache.jedis.JedisCache;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.QueryResultsRegion;
import upgrad.hibernate.cache.region.JedisRegion;

import java.util.Properties;

/**
 * @author : anuj.kumar
 **/
@Slf4j
public class JedisQueryResultRegion extends JedisRegion implements QueryResultsRegion {

    public JedisQueryResultRegion(JedisCache cache, Properties properties) {
        super(cache, properties);
    }

    @Override
    public Object get(Object key) throws CacheException {
        return cache.get(key);
    }

    @Override
    public void put(Object key, Object value) throws CacheException {
        cache.put(key, value);
    }

    @Override
    public void evict(Object key) throws CacheException {
        cache.remove(key);
    }

    @Override
    public void evictAll() throws CacheException {
        throw new UnsupportedOperationException("RedisQueryResultRegion#evictAll has not implemented yet!!");
    }
}
