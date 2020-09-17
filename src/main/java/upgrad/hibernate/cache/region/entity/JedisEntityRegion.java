package upgrad.hibernate.cache.region.entity;

import upgrad.hibernate.cache.jedis.JedisCache;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cfg.Settings;
import upgrad.hibernate.cache.region.transactional.JedisTransactionalRegion;
import upgrad.hibernate.cache.strategy.entity.NonStrictReadWriteJedisEntityRegionAccessStrategy;
import upgrad.hibernate.cache.strategy.entity.ReadOnlyJedisEntityRegionAccessStrategy;
import upgrad.hibernate.cache.strategy.entity.ReadWriteJedisEntityRegionAcessStrategy;

import java.util.Properties;

/**
 * @author : anuj.kumar
 **/
@Slf4j
public class JedisEntityRegion extends JedisTransactionalRegion implements EntityRegion {

	public JedisEntityRegion(JedisCache cache, Properties properties, CacheDataDescription metadata,
			Settings settings) {
		super(cache, properties, metadata, settings);
	}

	@Override
	public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
		log.debug("Creating EntityRegionAccessStrategy by accessType:{}", accessType);
		if (AccessType.READ_ONLY.equals(accessType)) {
			return new ReadOnlyJedisEntityRegionAccessStrategy(this, settings);
		} else if (AccessType.NONSTRICT_READ_WRITE.equals(accessType)) {
			return new NonStrictReadWriteJedisEntityRegionAccessStrategy(this, settings);
		} else if (AccessType.READ_WRITE.equals(accessType)) {
			return new ReadWriteJedisEntityRegionAcessStrategy(this, settings);
		}

		throw new UnsupportedOperationException("Hibernate-redis supports READ_ONLY and NONSTRICT_READ_WRITE as concurrency strategies only.");
	}

}
