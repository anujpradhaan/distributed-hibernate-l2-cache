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
public class NonStrictReadWriteJedisEntityRegionAccessStrategy extends AbstractJedisAccessStrategy<JedisEntityRegion>
		implements EntityRegionAccessStrategy {

	public NonStrictReadWriteJedisEntityRegionAccessStrategy(JedisEntityRegion region, Settings settings) {
		super(region, settings);
	}

	@Override
	public EntityRegion getRegion() {
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

	@Override
	public SoftLock lockItem(Object key, Object version) throws CacheException {
		return null;
	}

	@Override
	public void unlockItem(Object key, SoftLock lock) throws CacheException {
		region.remove(key);
	}

	@Override
	public boolean insert(Object key, Object value, Object version) throws CacheException {
		return false;
	}

	@Override
	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
		return false;
	}

	@Override
	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
		log.debug("called update by K:{}, V:{}", key, value);
		remove(key);
		return false;
	}

	@Override
	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException {
		return false;
	}

	@Override
	public void remove(Object key) throws CacheException {
		cache.remove(key);
	}
}
