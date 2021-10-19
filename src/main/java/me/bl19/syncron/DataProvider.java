package me.bl19.syncron;

/**
 * Interface for data providers
 */
public interface DataProvider {

    /**
     * This method is used to retrieve an object from the DataProvider
     * @param identifier The identifier key
     * @return The object that has been deserialized
     */
    Object retrieveObject(String identifier);

    /**
     * Fetches the time that a object was last updated at
     * @param identifier The identifier key
     * @return A long value for the time that the value for the key was last updated at
     */
    long lastUpdated(String identifier);

    /**
     * Sets a object and the time of the last change for a specified key
     * @param identifier The identifier key
     * @param value The new value for that key
     */
    void setObject(String identifier, Object value);

}
