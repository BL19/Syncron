package me.bl19.syncron;

import me.bl19.syncron.serializers.FastSerializationProvider;
import me.bl19.syncron.serializers.GsonSerializationProvider;
import org.nustaq.serialization.FSTConfiguration;

import java.lang.reflect.InaccessibleObjectException;

/**
 * Provides methods for creating SyncronizedObjects along with a DataProvider, SerializationProvider and other settings
 */
public class Syncron {

    static SerializationProvider defaultSerializationProvider;

    DataProvider dataProvider;
    SerializationProvider serializationProvider;
    long updateInterval = 5000;
    ThreadGroup updateThreadGroup = new ThreadGroup("Syncron-Update-Threads");

    /**
     * Create a new Syncron with a DataProvider
     * @param dataProvider The DataProvider for this Syncron
     */
    public Syncron(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
        this.serializationProvider = Syncron.defaultSerializationProvider();
    }

    /**
     * @return The data provider for this Syncron instance
     */
    public DataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * @return Returns the SerializationProvider for this instance, if none have been specified will return the default provider that was used upon creation of this Syncron instance.
     */
    public SerializationProvider getSerializationProvider() {
        return serializationProvider;
    }

    /**
     * Set the local SerializationProvider to use for the objects connected to this instance
     * @param serializationProvider The SerializationProvider to use
     */
    public void setSerializationProvider(SerializationProvider serializationProvider) {
        this.serializationProvider = serializationProvider;
    }

    /**
     * @return Milliseconds between checks of updated values
     */
    public long getUpdateInterval() {
        return updateInterval;
    }

    /**
     * Sets the rate that the SyncronizedObjects will refresh at
     * @param updateInterval The rate in milliseconds
     */
    public void setUpdateInterval(long updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * Creates a new SyncronizedObject instance for object syncronization without a default value
     * @param identifierKey The key to be unique to this object
     * @param <T> The object type for return statemets
     * @return A new SyncronizedObject with the specified key
     */
    public <T> SyncronizedObject<T> getSyncronizedObject(String identifierKey) {
        return new SyncronizedObject<>(this, identifierKey);
    }

    /**
     * Creates a new SyncronizedObject instance for object syncronization with a default value
     * @param identifierKey The key to be unique to this object
     * @param defaultObject The default value to write if no other value was found
     * @param <T> The object type for return statemets
     * @return A new SyncronizedObject with the specified key and default object
     */
    public <T> SyncronizedObject<T> getSyncronizedObject(String identifierKey, T defaultObject) {
        return new SyncronizedObject<>(this, identifierKey, defaultObject);
    }

    /**
     * Set the default serialization provider to use
     * @param defaultSerializationProvider The SerializationProvider to use as a default for when none is specified
     */
    public static void setDefaultSerializationProvider(SerializationProvider defaultSerializationProvider) {
        Syncron.defaultSerializationProvider = defaultSerializationProvider;
    }

    /**
     * @return Returns the default SerializationProvider that have been set, if none have been set it will find a compatible one.
     */
    public static SerializationProvider defaultSerializationProvider() {
        if(Syncron.defaultSerializationProvider == null) {
            Syncron.defaultSerializationProvider = findCompatibleSeralizationProvider();
        }
        return Syncron.defaultSerializationProvider;
    }

    static SerializationProvider findCompatibleSeralizationProvider() {
        try {
            return new FastSerializationProvider(FSTConfiguration.createDefaultConfiguration());
        } catch (InaccessibleObjectException ex) {} // This error occurs when running java 15+
        return new GsonSerializationProvider();
    }

}
