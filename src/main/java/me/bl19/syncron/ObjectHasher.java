package me.bl19.syncron;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Provides methods for hashing of objects
 */
public class ObjectHasher {

    /**
     * Creates a SHA-256 hash of an object using the specified SerializationProvider to convert the object to a hashable form
     * @param object The object to hash
     * @param serializationProvider The SerializationProvider to use for converting the object to a hashable form
     * @return The unique hash of that object
     */
    public static String createObjectSHA256(Object object, SerializationProvider serializationProvider) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        byte[] bytes = serializationProvider.serialize(object).getBytes(StandardCharsets.UTF_8);
        return bytesToHex(digest.digest(bytes));
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * Converts a byte array to uppercase hex
     * @param bytes The bytes to convert
     * @return A hex string
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
