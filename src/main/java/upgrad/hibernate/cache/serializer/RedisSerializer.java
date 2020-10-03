package upgrad.hibernate.cache.serializer;

import java.nio.charset.Charset;

public interface RedisSerializer<T> {

	byte[] EMPTY_BYTES = new byte[0];

	Charset UTF_8 = Charset.forName("UTF-8");

	/**
	 * Serialize Object
	 */
	byte[] serialize(final T graph);

	/**
	 * Deserialize to object
	 */
	T deserialize(final byte[] bytes);
}

