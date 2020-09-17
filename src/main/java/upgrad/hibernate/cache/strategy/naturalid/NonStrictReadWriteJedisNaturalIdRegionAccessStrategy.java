package upgrad.hibernate.cache.strategy.naturalid;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;
import upgrad.hibernate.cache.region.naturalid.JedisNaturalIdRegion;
import upgrad.hibernate.cache.strategy.AbstractJedisAccessStrategy;

@Slf4j
public class NonStrictReadWriteJedisNaturalIdRegionAccessStrategy extends AbstractJedisAccessStrategy<JedisNaturalIdRegion>
		implements NaturalIdRegionAccessStrategy {

	public NonStrictReadWriteJedisNaturalIdRegionAccessStrategy(JedisNaturalIdRegion region, Settings settings) {
		super(region, settings);
	}

	@Override
	public NaturalIdRegion getRegion() {
		return this.region;
	}

	@Override
	public Object get(Object key, long txTimestamp) throws CacheException {
		log.debug("Called get for Key:{}", key);
		return cache.get(key);
	}

	@Override
	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
			throws CacheException {
		log.debug("called putFromLoad by Key:{}, Value:{}", key, value);
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
	@Override
	public SoftLock lockItem(Object key, Object version) throws CacheException {
		return null;
	}

	/**
	 * Since this is a non-strict read/write strategy item locking is not used.
	 */
	@Override
	public void unlockItem(Object key, SoftLock lock) throws CacheException {
		region.remove(key);
	}

	/**
	 * Returns <code>false</code> since this is an asynchronous cache access strategy.
	 */
	@Override
	public boolean insert(Object key, Object value) throws CacheException {
		return false;
	}

	/**
	 * Returns <code>false</code> since this is a non-strict read/write cache access strategy
	 */
	@Override
	public boolean afterInsert(Object key, Object value) throws CacheException {
		return false;
	}

	/**
	 * Removes the entry since this is a non-strict read/write cache strategy.
	 */
	@Override
	public boolean update(Object key, Object value) throws CacheException {
		log.debug("called update by K:{}, V:{}", key, value);
		remove(key);
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(Object key) throws CacheException {
		cache.remove(key);
	}
}
