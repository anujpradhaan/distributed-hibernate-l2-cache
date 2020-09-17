package upgrad.hibernate.cache.jedis;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;
import redis.clients.jedis.JedisPool;
import upgrad.hibernate.cache.region.collection.JedisCollectionRegion;
import upgrad.hibernate.cache.region.entity.JedisEntityRegion;
import upgrad.hibernate.cache.region.naturalid.JedisNaturalIdRegion;
import upgrad.hibernate.cache.region.query.JedisQueryResultRegion;
import upgrad.hibernate.cache.region.timestamp.JedisTimestampsRegion;

import java.util.Properties;

@Slf4j
public class JedisRegionFactory implements RegionFactory {

	private JedisPool pool;

	private Properties properties;

	private Settings settings;

	//	public JedisRegionFactory(Properties properties) {
	//		this.properties = properties;
	//	}

	@Override
	public void start(Settings settings, Properties properties) throws CacheException {
		this.settings = settings;
		this.properties = properties;
		log.info("Initializing Jedis with properties {}", properties);
		this.pool = JedisConnectionFactory.getJedisPool(properties);
	}

	@Override
	public void stop() {
		this.pool.destroy();
	}

	@Override
	public boolean isMinimalPutsEnabledByDefault() {
		return true;
	}

	@Override
	public AccessType getDefaultAccessType() {
		return AccessType.READ_WRITE;
	}

	@Override
	public long nextTimestamp() {
		return System.currentTimeMillis() / 100;
	}

	@Override
	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
		return new JedisEntityRegion(getRedisCache(regionName), properties, metadata, settings);
	}

	@Override
	public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
		return new JedisNaturalIdRegion(getRedisCache(regionName), properties, metadata, settings);
	}

	@Override
	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata)
			throws CacheException {
		return new JedisCollectionRegion(getRedisCache(regionName), properties, metadata, settings);
	}

	@Override
	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
		return new JedisQueryResultRegion(getRedisCache(regionName), properties);
	}

	@Override
	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
		return new JedisTimestampsRegion(getRedisCache(regionName), properties);
	}

	private JedisCache getRedisCache(String regionName) {
		log.debug("Creating a connection Pool for region:{}", regionName);
		return new JedisCacheImpl(pool, regionName);
	}

}
