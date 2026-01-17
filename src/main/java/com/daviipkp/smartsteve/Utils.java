package com.daviipkp.smartsteve;

import com.daviipkp.SteveCommandLib.instance.Command;
import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static Reflections ref = new Reflections("com.daviipkp.smartsteve");

    public static String getCommandNamesWithDesc() {
       StringBuilder builder = new StringBuilder();
       for(Class<?> c : getRegisteredCommands()) {
           builder.append(SteveJsoning.generateGuide(c));
       }
       return builder.toString();
    }

    public static Set<Class<?>> getRegisteredCommands() {
        Set<Class<?>> classes = ref.getTypesAnnotatedWith(CommandDescription.class);
        return classes;
    }

    public static Command getCommandByName(String s) throws InstantiationException, IllegalAccessException {
        Set<Class<?>> classes = ref.getTypesAnnotatedWith(CommandDescription.class);
        for(Class<?> c : classes) {
            if(c.getSimpleName().equals(s)) {
                return (Command) c.newInstance();
            }
        }
        System.out.println("Command not found:" + s);
        return null;
    }

    public static String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
