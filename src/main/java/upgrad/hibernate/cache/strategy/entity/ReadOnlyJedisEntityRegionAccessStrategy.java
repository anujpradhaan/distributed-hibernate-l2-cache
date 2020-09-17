package upgrad.hibernate.cache.strategy.entity;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;
import upgrad.hibernate.cache.region.entity.JedisEntityRegion;
import upgrad.hibernate.cache.strategy.AbstractJedisAccessStrategy;

@Slf4j
public class ReadOnlyJedisEntityRegionAccessStrategy extends AbstractJedisAccessStrategy<JedisEntityRegion>
		implements EntityRegionAccessStrategy {

	public ReadOnlyJedisEntityRegionAccessStrategy(JedisEntityRegion region, Settings settings) {
		super(region, settings);
	}

	@Override
	public EntityRegion getRegion() {
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

	public boolean insert(Object key, Object value, Object version) throws CacheException {
		return false;
	}

	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
		cache.put(key, value);
		return true;
	}

	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't write to a readonly object");
	}

	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Can't write to a readonly object");
	}
}

