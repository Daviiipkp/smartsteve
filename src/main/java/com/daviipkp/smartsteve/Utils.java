package com.daviipkp.smartsteve;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String extractJsonField(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\"[^\"]*\"|null|true|false)");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            String value = matcher.group(1);
            if (value.startsWith("\"") && value.endsWith("\"")) {
                return value.substring(1, value.length() - 1);
            }
            return value;
        }
        return null;
    }
}
