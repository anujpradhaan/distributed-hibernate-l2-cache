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
public class ReadOnlyJedisCollectionRegionAccessStrategy extends AbstractJedisAccessStrategy<JedisCollectionRegion>
		implements CollectionRegionAccessStrategy {

	public ReadOnlyJedisCollectionRegionAccessStrategy(JedisCollectionRegion region, Settings settings) {
		super(region, settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectionRegion getRegion() {
		return region;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object get(Object key, long txTimestamp) throws CacheException {
		log.debug("called get by K:{}", key);
		return cache.get(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	 * Throws UnsupportedOperationException since this cache is read-only
	 *
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public SoftLock lockItem(Object key, Object version) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't write to a readonly object");
	}

	/**
	 * A no-op since this cache is read-only
	 */
	@Override
	public void unlockItem(Object key, SoftLock lock) throws CacheException {
	}
}
