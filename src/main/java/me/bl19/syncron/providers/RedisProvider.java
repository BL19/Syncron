package me.bl19.syncron.providers;

import me.bl19.syncron.DataProvider;
import me.bl19.syncron.SerializationProvider;
import me.bl19.syncron.Syncron;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

/**
 * Provides a way to store objects in redis for quick access
 */
public class RedisProvider implements DataProvider {

    static final String DATA_EXTENSION = ".Data";
    static final String LAST_CHANGE_EXTENSION = ".LastUpdate";
    final Jedis jedis;
    SerializationProvider serializationProvider;

    /**
     * Use the static methods for creating a instance if you are not absolutely sure about what you are doing.
     * @param jedis The jedis instance to use
     * @param serializationProvider The SerializationProvider to use for serialization and deserialization
     */
    public RedisProvider(Jedis jedis, SerializationProvider serializationProvider) {
        this.jedis = jedis;
        this.serializationProvider = serializationProvider;
    }

    /**
     * Creates a new RedisProvider with a host and optionally a port, the port will default to 6379 if none has been specified.
     * @param connectionUrl The url to use for connecting to redis
     * @return A new RedisProvider to use with a Syncron
     */
    public static RedisProvider fromConnectionUrl(String connectionUrl) {
        if(!connectionUrl.contains(":")) {
            connectionUrl = connectionUrl + ":6379"; // Default redis port
        }
        return new RedisProvider(new Jedis(HostAndPort.parseString(connectionUrl)), Syncron.defaultSerializationProvider());
    }
    /**
     * Creates a new RedisProvider with a host and optionally a port, the port will default to 6379 if none has been specified.
     * @param connectionUrl The url to use for connecting to redis
     * @param serializationProvider The SerializationProvider to use to serialize and deserialize the data
     * @return A new RedisProvider to use with a Syncron
     */
    public static RedisProvider fromConnectionUrl(String connectionUrl, SerializationProvider serializationProvider) {
        if(!connectionUrl.contains(":")) {
            connectionUrl = connectionUrl + ":6379"; // Default redis port
        }
        return new RedisProvider(new Jedis(HostAndPort.parseString(connectionUrl)), serializationProvider);
    }

    @Override
    public Object retrieveObject(String identifier) {
        String redisString;
        synchronized (jedis) {
            redisString = jedis.get(identifier + DATA_EXTENSION);
        }
        return serializationProvider.deserialize(redisString);
    }

    @Override
    public long lastUpdated(String identifier) {
        String redisString;
        synchronized (jedis) {
            redisString = jedis.get(identifier + LAST_CHANGE_EXTENSION);
        }
        if(redisString == null) return -1;
        return Long.parseLong(redisString);
    }

    @Override
    public void setObject(String identifier, Object value) {
        String base64Data = serializationProvider.serialize(value);
        synchronized (jedis) {
            jedis.set(identifier + DATA_EXTENSION, base64Data);
            jedis.set(identifier + LAST_CHANGE_EXTENSION, System.currentTimeMillis() + "");
        }
    }
}
