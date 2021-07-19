package az.code.turalbot.utils;

import java.util.UUID;

public class GenerateUUID {

    public static String generateUUID(){
        final String uuid = UUID.randomUUID().toString()
                .replace("-", "");
        return uuid;
    }

}
