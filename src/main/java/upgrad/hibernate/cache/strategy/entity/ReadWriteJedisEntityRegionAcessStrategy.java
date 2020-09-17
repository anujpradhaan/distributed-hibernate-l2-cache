package upgrad.hibernate.cache.strategy.entity;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;
import upgrad.hibernate.cache.region.entity.JedisEntityRegion;
import upgrad.hibernate.cache.strategy.AbstractReadWriteJedisAccessStrategy;

/**
 * User: jtlee
 * Date: 3/5/13
 * Time: 2:52 PM
 */
public class ReadWriteJedisEntityRegionAcessStrategy extends AbstractReadWriteJedisAccessStrategy<JedisEntityRegion> implements EntityRegionAccessStrategy {

    /**
     * Create an access strategy wrapping the given region.
     */
    public ReadWriteJedisEntityRegionAcessStrategy(JedisEntityRegion region, Settings settings) {
        super(region, settings);
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

    @Override
    public EntityRegion getRegion() {
        return region;
    }


}
