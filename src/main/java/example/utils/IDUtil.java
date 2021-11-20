package example.utils;

import java.util.UUID;

public class IDUtil {
    public static String getUUId() {
        return UUID.randomUUID().toString();
    }
}
