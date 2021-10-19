package me.bl19.syncron.serializers;

import com.google.gson.Gson;
import me.bl19.syncron.SerializationProvider;

/**
 * Serializes objects using GSON
 */
public class GsonSerializationProvider implements SerializationProvider {
    Gson gson;

    /**
     * Creates a GsonSerializationProvider with the default Gson instance created from "new Gson()"
     */
    public GsonSerializationProvider() {
        this(new Gson());
    }

    /**
     * Creates a GsonSerializationProvider with a custom Gson instance
     * @param gson The gson instance to use
     */
    public GsonSerializationProvider(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String serialize(Object object) {
        return object.getClass().getName() + ";" + gson.toJson(object);
    }

    @Override
    public Object deserialize(String string) {
        if(!string.contains(";")) {
            System.err.println("Invalid storage syntax!");
            return null;
        }
        String type = string.substring(0, string.indexOf(";"));
        String data = string.substring(type.length() + 1);
        try {
            return gson.fromJson(data, Class.forName(type));
        } catch (ClassNotFoundException e) {
            System.err.println("Unable to deserialize");
            e.printStackTrace();
            return null;
        }
    }
}
