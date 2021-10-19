import me.bl19.syncron.Syncron;
import me.bl19.syncron.SyncronizedObject;
import me.bl19.syncron.providers.RedisProvider;
import me.bl19.syncron.serializers.GsonSerializationProvider;
import me.bl19.syncron.serializers.JavaSerializationProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class RedisProviderTests {

    static Syncron syncron;

    @BeforeAll
    public static void preTest() {
        Syncron.setDefaultSerializationProvider(new GsonSerializationProvider()); // This is to test the different serializers
        syncron = new Syncron(RedisProvider.fromConnectionUrl("192.168.0.221"));
        syncron.setUpdateInterval(2000);
    }

    @Order(1)
    @Test
    public void canSaveObject() {
        long saveStart = System.currentTimeMillis();
        SyncronizedObject<TestingClass> testSyncron = syncron.getSyncronizedObject("testobject1", TestingClass.createSample());
        testSyncron.set(TestingClass.createSample());
        long saveTime = System.currentTimeMillis() - saveStart;
        System.out.println("Saving took: " + saveTime);
        try {
            printObject(testSyncron.get());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Order(2)
    @Test
    public void canChangeObject() {
        long changeStart = System.currentTimeMillis();
        SyncronizedObject<TestingClass> testSyncron = syncron.getSyncronizedObject("testobject1", TestingClass.createSample());
        SyncronizedObject<TestingClass> testSyncron1 = syncron.getSyncronizedObject("testobject1", TestingClass.createSample());
        TestingClass testSyncron1Pre = testSyncron1.get();
        testSyncron.get().aString = "But is it really?";
        try {
            printObject(testSyncron.get());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            printObject(testSyncron1Pre);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Post sync");
        try {
            printObject(testSyncron.get());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            printObject(testSyncron1Pre);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        long changeTimeElapsed = System.currentTimeMillis() - changeStart;
        System.out.println("Changing took: " + changeTimeElapsed);
    }

    public void printObject(Object object) throws IllegalAccessException {
        System.out.println("{");
        printObject(object, "  ");
        System.out.println("}");
    }

    public void printObject(Object object, String indent) throws IllegalAccessException {
        for (Field field : object.getClass().getFields()) {
            if((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) continue;
            if(field.getType().getPackageName().startsWith("java.lang")) {
                System.out.println(indent + field.getName() + " = " + field.get(object));
            } else {
                System.out.println(indent + field.getName() + " = {");
                printObject(field.get(object), indent + "  ");
                System.out.println(indent + "}");
            }
        }
    }

    public static class TestingClass implements Serializable {
        public String aString;
        public long aLong;
        public boolean aBoolean;
        public double aDouble;
        public byte aByte;

        public static TestingClass createSample() {
            TestingClass newClass = new TestingClass();
            newClass.aString = "This is a string";
            newClass.aLong = 123456;
            newClass.aBoolean = true;
            newClass.aDouble = Math.PI;
            newClass.aByte = 120;
            return newClass;
        }


    }

}
