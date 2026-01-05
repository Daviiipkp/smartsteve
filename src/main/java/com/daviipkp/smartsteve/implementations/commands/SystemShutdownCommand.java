package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartsteve.Instance.CommandE;
import com.daviipkp.smartsteve.services.CommandRegistry;
import com.daviipkp.smartsteve.services.EarService;
import org.springframework.stereotype.Component;

import java.io.IOException;

@CommandDescription(value = "Command designed to shutdown the computer/system.", possibleArguments = "Time in seconds for it to shutdown. No argument means instantly.")
public class SystemShutdownCommand extends InstantCommand {

    public SystemShutdownCommand(String... args) {
        setCommand(new Runnable() {
            public void run() {
                ProcessBuilder pb = new ProcessBuilder("shutdown", "/s", "/f", "/t", (args.length == 0) ? "0" : args[0]);
                try {
                    pb.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void handleError(Exception e) {
    }
}
