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
public class ReadOnlyJedisNaturalIdRegionAccessStrategy extends AbstractJedisAccessStrategy<JedisNaturalIdRegion>
		implements NaturalIdRegionAccessStrategy {

	public ReadOnlyJedisNaturalIdRegionAccessStrategy(JedisNaturalIdRegion region, Settings settings) {
		super(region, settings);
	}

	@Override
	public NaturalIdRegion getRegion() {
		return region;
	}

	@Override
	public Object get(Object key, long txTimestamp) throws CacheException {
		log.debug("Called get by Key:{}", key);
		return cache.get(key);
	}

	@Override
	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
		log.debug("Called put by Key:{}, Value:{}", key, value);
		if (minimalPutOverride && region.contains(key)) {
			return false;
		} else {
			cache.put(key, value);
			return true;
		}
	}

	@Override
	public SoftLock lockItem(Object key, Object version) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't write to a readonly object");
	}

	public void unlockItem(Object key, SoftLock lock) throws CacheException {
		//throw new UnsupportedOperationException("Can't write to a readonly object");
	}

	@Override
	public boolean insert(Object key, Object value) throws CacheException {
		return false;
	}

	@Override
	public boolean afterInsert(Object key, Object value) throws CacheException {
		cache.put(key, value);
		return true;
	}

	@Override
	public boolean update(Object key, Object value) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't write to a readonly object");
	}

	@Override
	public boolean afterUpdate(Object key, Object value, SoftLock lock)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't write to a readonly object");
	}
}

