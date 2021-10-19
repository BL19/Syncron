package me.bl19.syncron.serializers;

import me.bl19.syncron.SerializationProvider;

import java.io.*;
import java.util.Base64;

/**
 * Uses the java object serialization for serialization
 */
public class JavaSerializationProvider implements SerializationProvider {

    @Override
    public String serialize(Object object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            byte[] objectBytes = bos.toByteArray();
            String base64Data = Base64.getEncoder().encodeToString(objectBytes);
            return base64Data;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    @Override
    public Object deserialize(String string) {
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(string));
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            return o;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }
}
