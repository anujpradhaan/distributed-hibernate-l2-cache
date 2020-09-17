package upgrad.hibernate.cache.strategy.collection;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;
import upgrad.hibernate.cache.region.collection.JedisCollectionRegion;
import upgrad.hibernate.cache.strategy.AbstractReadWriteJedisAccessStrategy;

/**
 * User: jtlee
 * Date: 3/5/13
 * Time: 1:50 PM
 */
public class ReadWriteJedisCollectionRegionAcessStrategy extends AbstractReadWriteJedisAccessStrategy<JedisCollectionRegion> implements CollectionRegionAccessStrategy {

    /**
     * Create an access strategy wrapping the given region.
     */
    public ReadWriteJedisCollectionRegionAcessStrategy(JedisCollectionRegion region, Settings settings) {
        super(region, settings);
    }

    @Override
    public CollectionRegion getRegion() {
        return region;
    }

    @Override
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        //Do not support client-side locking
        return null;
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        //Do not support client-side locking
    }
}
