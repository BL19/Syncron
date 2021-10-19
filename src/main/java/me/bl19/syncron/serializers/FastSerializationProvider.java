package me.bl19.syncron.serializers;

import me.bl19.syncron.SerializationProvider;
import org.nustaq.serialization.FSTConfiguration;

import java.util.Base64;

/**
 * Uses the fast-serialization library for serialization
 */
public class FastSerializationProvider implements SerializationProvider {

    FSTConfiguration fstConfiguration;

    /**
     * Creates a new FastSerializationProvider with a config
     * @param fstConfiguration The configuration
     */
    public FastSerializationProvider(FSTConfiguration fstConfiguration) {
        this.fstConfiguration = fstConfiguration;
    }

    @Override
    public String serialize(Object object) {
        byte[] objectBytes = fstConfiguration.asByteArray(object);
        String base64Data = Base64.getEncoder().encodeToString(objectBytes);
        return base64Data;
    }

    @Override
    public Object deserialize(String string) {
        byte[] objectBytes = Base64.getDecoder().decode(string);
        return fstConfiguration.asObject(objectBytes);
    }
}
