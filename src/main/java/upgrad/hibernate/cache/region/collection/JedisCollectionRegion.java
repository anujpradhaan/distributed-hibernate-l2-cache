package upgrad.hibernate.cache.region.collection;

import upgrad.hibernate.cache.jedis.JedisCache;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cfg.Settings;
import upgrad.hibernate.cache.region.transactional.JedisTransactionalRegion;
import upgrad.hibernate.cache.strategy.collection.NonStrictReadWriteJedisCollectionRegionAccessStrategy;
import upgrad.hibernate.cache.strategy.collection.ReadOnlyJedisCollectionRegionAccessStrategy;
import upgrad.hibernate.cache.strategy.collection.ReadWriteJedisCollectionRegionAcessStrategy;

import java.util.Properties;

/**
 * @author : anuj.kumar
 **/
@Slf4j
public class JedisCollectionRegion extends JedisTransactionalRegion implements CollectionRegion {

	public JedisCollectionRegion(JedisCache cache, Properties properties, CacheDataDescription metadata,
			Settings settings) {
		super(cache, properties, metadata, settings);
	}

	@Override
	public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
		log.debug("Creating CollectionRegionAccessStrategy by accessType:{}", accessType);
		if (AccessType.READ_ONLY.equals(accessType)) {
			return new ReadOnlyJedisCollectionRegionAccessStrategy(this, settings);
		} else if (AccessType.NONSTRICT_READ_WRITE.equals(accessType)) {
			return new NonStrictReadWriteJedisCollectionRegionAccessStrategy(this, settings);
		} else if (AccessType.READ_WRITE.equals(accessType)) {
			return new ReadWriteJedisCollectionRegionAcessStrategy(this, settings);
		}

		throw new UnsupportedOperationException("Hibernate-redis supports READ_ONLY and NONSTRICT_READ_WRITE as concurrency strategies only.");
	}
}

