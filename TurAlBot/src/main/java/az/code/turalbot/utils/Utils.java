package az.code.turalbot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static boolean regexForCountOfPeople(String count){
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(count);
        return matcher.matches();
    }
}
