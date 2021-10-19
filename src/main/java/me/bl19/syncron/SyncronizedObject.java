package me.bl19.syncron;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A object wrapper used to syncronize a object between one or more applications using a DataProvider
 * @param <T> The type of object, used for return methods.
 */
public class SyncronizedObject<T> {

    String identifierKey;
    T localCopy;
    long lastSyncronized;
    Syncron syncron;
    String lastHash = null;
    Thread updateThread;

    /**
     * Creates a new SyncronizedObject with a identifier and no default value
     * @param syncron The syncron to use for DataProvider and updateInterval
     * @param identifierKey The key to store and load the data from
     */
    public SyncronizedObject(Syncron syncron, String identifierKey) {
        this(syncron, identifierKey, null);
    }

    /**
     * Creates a new SyncronizedObject with a identifier and a default value
     * @param syncron The syncron to use for DataProvider and updateInterval
     * @param identifierKey The key to store and load the data from
     * @param defaultValue The default value to store if none was found from the DataProvider
     */
    public SyncronizedObject(Syncron syncron, String identifierKey, T defaultValue) {
        this.identifierKey = identifierKey;
        this.localCopy = defaultValue;
        this.syncron = syncron;
        this.updateThread = new Thread(syncron.updateThreadGroup, new Runnable() {
            @Override
            public void run() {
                long lastCheck = System.currentTimeMillis();
                while (true) {
                    try {
                        if(lastCheck + syncron.updateInterval < System.currentTimeMillis()) {
                            lastCheck = System.currentTimeMillis();
                            checkUpdates();
                        }
                        Thread.sleep(100);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "Syncron-Update-Thread-" + identifierKey);
        this.updateThread.start();
    }

    /**
     * Checks for updates to the data and returns the object
     * @return The object associated with this key
     */
    public T get() {
        checkUpdates();
        return localCopy;
    }

    void checkUpdates() {
        long lastUpdate = syncron.getDataProvider().lastUpdated(identifierKey);
        if(lastUpdate == -1) {
            syncron.getDataProvider().setObject(identifierKey, localCopy);
            return;
        }
        if(lastUpdate > lastSyncronized) {
            lastSyncronized = lastUpdate;
            Object newInstance = syncron.getDataProvider().retrieveObject(identifierKey);
            if(newInstance == null) {
                localCopy = null;
                return;
            }
            try {
                if(newInstance.getClass().getPackageName().startsWith("java.lang.")) {
                    localCopy = (T) newInstance; // This is if the object is a double, String, boolean, long or any default java type
                } else {
                    deepReplace(newInstance, localCopy);
                }
                return;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        String objectHash = ObjectHasher.createObjectSHA256(localCopy, syncron.getSerializationProvider());
        if(!objectHash.equals(lastHash)) {
            syncron.getDataProvider().setObject(identifierKey, localCopy);
        }
    }

    void deepReplace(Object src, Object target) throws NoSuchFieldException, IllegalAccessException {

        for (Field field : src.getClass().getFields()) {
            if((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) continue;
            Field targetField = target.getClass().getField(field.getName());
            if(targetField.getType().getPackageName().startsWith("java.lang")) {
                targetField.set(target, field.get(src));
            } else {
                deepReplace(field.get(src), targetField.get(target));
            }
        }
    }

    /**
     * Set the value in the DataProvider and the local copy of it, this will set a new value for when last changed.
     * @param newValue The new data value to use
     */
    public void set(T newValue) {
        localCopy = newValue;
        syncron.getDataProvider().setObject(identifierKey, newValue);
        lastSyncronized = System.currentTimeMillis();
    }

}
