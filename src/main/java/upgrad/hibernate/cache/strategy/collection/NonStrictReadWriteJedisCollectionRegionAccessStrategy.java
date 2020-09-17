package upgrad.hibernate.cache.strategy.collection;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;
import upgrad.hibernate.cache.region.collection.JedisCollectionRegion;
import upgrad.hibernate.cache.strategy.AbstractJedisAccessStrategy;

@Slf4j
public class NonStrictReadWriteJedisCollectionRegionAccessStrategy extends AbstractJedisAccessStrategy<JedisCollectionRegion>
		implements CollectionRegionAccessStrategy {


	public NonStrictReadWriteJedisCollectionRegionAccessStrategy(JedisCollectionRegion region, Settings settings) {
		super(region, settings);
	}

	@Override
	public CollectionRegion getRegion() {
		return region;
	}

	@Override
	public Object get(Object key, long txTimestamp) throws CacheException {
		log.debug("called get by K:{}", key);
		return cache.get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
			throws CacheException {
		log.debug("called putFromLoad by K:{}, V:{}", key, value);
		if (minimalPutOverride && region.contains(key)) {
			return false;
		} else {
			cache.put(key, value);
			return true;
		}
	}

	/**
	 * Since this is a non-strict read/write strategy item locking is not used.
	 */
	public SoftLock lockItem(Object key, Object version) throws CacheException {
		return null;
	}

	/**
	 * Since this is a non-strict read/write strategy item locking is not used.
	 */
	public void unlockItem(Object key, SoftLock lock) throws CacheException {
//		LOG.debug("called unlockItem K:{}", key);
//		region.remove(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(Object key) throws CacheException {
		log.debug("called remove K:{}", key);
		region.remove(key);
	}
}

