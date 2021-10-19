package me.bl19.syncron;

/**
 * Provides methods for serializing and deserializing of objects
 */
public interface SerializationProvider {

    /**
     * Serializes a object to a string
     * @param object The object to serialize
     * @return A string that can be deserialized into a object with the same values
     */
    String serialize(Object object);

    /**
     * Deserializes a string into an object
     * @param string The string to try and deserialize
     * @return The object that has been deserialized from the string
     */
    Object deserialize(String string);

}
