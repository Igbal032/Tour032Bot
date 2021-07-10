package az.code.turalbot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static boolean regexForCountOfPeople(String count){
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(count);
        return matcher.matches();
    }
    public static boolean regexForDate(String date){
        Pattern pattern = Pattern.compile("[0-3][0-9]-[0-1][0-9]-[\\d]{4}");
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }
}
