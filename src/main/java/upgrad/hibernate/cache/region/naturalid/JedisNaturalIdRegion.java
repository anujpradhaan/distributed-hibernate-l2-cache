package upgrad.hibernate.cache.region.naturalid;

import upgrad.hibernate.cache.jedis.JedisCache;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cfg.Settings;
import upgrad.hibernate.cache.region.transactional.JedisTransactionalRegion;
import upgrad.hibernate.cache.strategy.naturalid.NonStrictReadWriteJedisNaturalIdRegionAccessStrategy;
import upgrad.hibernate.cache.strategy.naturalid.ReadOnlyJedisNaturalIdRegionAccessStrategy;
import upgrad.hibernate.cache.strategy.naturalid.ReadWriteJedisNaturalIdRegionAcessStrategy;

import java.util.Properties;

/**
 * @author : anuj.kumar
 **/
@Slf4j
public class JedisNaturalIdRegion extends JedisTransactionalRegion implements NaturalIdRegion {

	public JedisNaturalIdRegion(
			JedisCache cache, Properties properties, CacheDataDescription metadata, Settings settings) {
		super(cache, properties, metadata, settings);
	}

	@Override
	public NaturalIdRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
		log.debug("Creating NaturalIdRegionAccessStrategy by accessType:{}", accessType);
		if (AccessType.READ_ONLY.equals(accessType)) {
			return new ReadOnlyJedisNaturalIdRegionAccessStrategy(this, settings);
		} else if (AccessType.NONSTRICT_READ_WRITE.equals(accessType)) {
			return new NonStrictReadWriteJedisNaturalIdRegionAccessStrategy(this, settings);
		} else if (AccessType.READ_WRITE.equals(accessType)) {
			return new ReadWriteJedisNaturalIdRegionAcessStrategy(this, settings);
		}

		throw new UnsupportedOperationException("Hibernate-redis supports READ_ONLY and NONSTRICT_READ_WRITE as concurrency strategies only.");
	}
}
