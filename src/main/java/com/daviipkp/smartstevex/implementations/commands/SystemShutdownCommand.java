package com.daviipkp.smartstevex.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CommandDescription(value = "Command designed to shutdown the computer/system.")
public class SystemShutdownCommand extends InstantCommand {

    @Describe(description = "<Time in seconds>")
    private String time;

    public SystemShutdownCommand() {
        setCommand(new Runnable() {
            public void run() {
                String delay = (time == null || time.isEmpty()) ? "0" : time;

                String os = System.getProperty("os.name").toLowerCase();
                List<String> command = new ArrayList<>();

                if (os.contains("win")) {
                    command.add("shutdown");
                    command.add("/s");
                    command.add("/f");
                    command.add("/t");
                    command.add(delay);
                } else if (os.contains("nix") || os.contains("nux") || os.contains("aix") || os.contains("mac")) {
                    command.add("/bin/sh");
                    command.add("-c");
                    command.add("sleep " + delay + "; shutdown -h now");
                } else {
                    throw new UnsupportedOperationException("OS does not support this operation.");
                }

                ProcessBuilder pb = new ProcessBuilder(command);
                try {
                    pb.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void handleError(Exception e) {}
}