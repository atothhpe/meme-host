package xyz.andrastoth.memehost;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

public class Util {

    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public static void tryCloseStream(Closeable closeable) {
        if(closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}