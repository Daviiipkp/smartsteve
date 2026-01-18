package com.daviipkp.smartstevex;

import com.daviipkp.SteveCommandLib.instance.Command;
import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;

public class Utils {


    public static String getCommandNamesWithDesc() {
       StringBuilder builder = new StringBuilder();
       for(Class<? extends Command> c : SmartsteveApplication.getCommandList()) {
           builder.append(SteveJsoning.generateGuide(c));
       }
       return builder.toString();
    }

    public static List<Class<? extends Command>> getRegisteredCommands(String... packages) {
        List<Class<? extends Command>> list = new ArrayList<>();
        for(String s : packages) {
            for(Class<?> c : new Reflections(s).getTypesAnnotatedWith(CommandDescription.class)) {
                if(Command.class.isAssignableFrom(c)) {
                    list.add(c.asSubclass(Command.class));
                }else {
                    System.out.println(">>> Ignoring command '" + c.getName() + "' because it does not extend Command class.");
                }
            }
        }
        return list;
    }

    public static Command getCommandByName(String cmd) throws InstantiationException, IllegalAccessException {
        for(Class<? extends Command> c : SmartsteveApplication.getCommandList()) {
            if(c.getSimpleName().equals(cmd)) {
                return c.newInstance();
            }
        }
        return null;
    }

}
