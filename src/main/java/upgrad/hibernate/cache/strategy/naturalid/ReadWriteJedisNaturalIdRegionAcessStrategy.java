package upgrad.hibernate.cache.strategy.naturalid;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;
import upgrad.hibernate.cache.region.naturalid.JedisNaturalIdRegion;
import upgrad.hibernate.cache.strategy.AbstractReadWriteJedisAccessStrategy;

/**
 * User: jtlee
 * Date: 3/5/13
 * Time: 2:52 PM
 */
public class ReadWriteJedisNaturalIdRegionAcessStrategy extends AbstractReadWriteJedisAccessStrategy<JedisNaturalIdRegion> implements
        NaturalIdRegionAccessStrategy {

    /**
     * Create an access strategy wrapping the given region.
     */
    public ReadWriteJedisNaturalIdRegionAcessStrategy(JedisNaturalIdRegion region, Settings settings) {
        super(region, settings);
    }

    @Override
    public NaturalIdRegion getRegion() {
        return region;
    }

    @Override public boolean insert(Object o, Object o1) throws CacheException {
        return false;
    }

    @Override public boolean afterInsert(Object o, Object o1) throws CacheException {
        return false;
    }

    @Override public boolean update(Object o, Object o1) throws CacheException {
        return false;
    }

    @Override public boolean afterUpdate(Object o, Object o1, SoftLock softLock) throws CacheException {
        return false;
    }

    @Override
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        //Do not support client side lock
        return null;
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        //Do not support client side lock
    }


}
