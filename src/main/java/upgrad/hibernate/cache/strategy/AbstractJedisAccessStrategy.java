package upgrad.hibernate.cache.strategy;

import upgrad.hibernate.cache.jedis.JedisCache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;
import upgrad.hibernate.cache.region.transactional.JedisTransactionalRegion;


public abstract class AbstractJedisAccessStrategy<T extends JedisTransactionalRegion> {

	protected final T region;

	protected final Settings settings;

	protected final JedisCache cache;

	protected AbstractJedisAccessStrategy(T region, Settings settings) {
		this.region = region;
		this.settings = settings;
		this.cache = region.getRedisCache();
	}

	public final boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
		return putFromLoad(key, value, txTimestamp, version, settings.isMinimalPutsEnabled());
	}

	public abstract boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
			throws CacheException;

	public final SoftLock lockRegion() {
		return null;
	}

	public final void unlockRegion(SoftLock lock) throws CacheException {
	}

	public void remove(Object key) throws CacheException {
	}

	public final void removeAll() throws CacheException {
//		region.clear();
	}

	public final void evict(Object key) throws CacheException {
		region.remove(key);
	}

	public final void evictAll() throws CacheException {
//		region.clear();
	}
}

