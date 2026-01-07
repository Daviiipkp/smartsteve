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

}
