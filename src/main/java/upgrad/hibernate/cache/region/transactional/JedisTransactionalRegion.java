package upgrad.hibernate.cache.region.transactional;

import upgrad.hibernate.cache.jedis.JedisCache;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.TransactionalDataRegion;
import org.hibernate.cfg.Settings;
import upgrad.hibernate.cache.region.JedisRegion;

import java.util.Properties;

/**
 * @author : anuj.kumar
 **/
@Slf4j
public class JedisTransactionalRegion extends JedisRegion implements TransactionalDataRegion {

	/**
	 * Hibernate settings associated with the persistence unit.
	 */
	protected final Settings settings;

	/**
	 * Metadata associated with the objects stored in the region.
	 */
	protected final CacheDataDescription metadata;

	public JedisTransactionalRegion( JedisCache cache, Properties properties,
	                         CacheDataDescription metadata, Settings settings) {
		super(cache, properties);
		this.metadata = metadata;
		this.settings = settings;
	}

	@Override
	public boolean isTransactionAware() {
		return false;
	}

	@Override
	public CacheDataDescription getCacheDataDescription() {
		return metadata;
	}

	public Settings getSettings() {
		return settings;
	}

	public final void remove(Object key) {
		cache.remove(key);
	}
}

