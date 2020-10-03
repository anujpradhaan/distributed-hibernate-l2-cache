package upgrad.hibernate.cache.serializer;

import org.springframework.util.StringUtils;

/**
 * @author : anuj.kumar
 **/
public class StringRedisSerializer implements RedisSerializer<String> {
	@Override
	public byte[] serialize(String content) {
		return (StringUtils.isEmpty(content)) ? EMPTY_BYTES : content.getBytes(UTF_8);
	}

	@Override public String deserialize(byte[] bytes) {
		return (bytes == null || bytes.length == 0) ? "" : new String(bytes, UTF_8);
	}
}
