package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.smartsteve.Instance.Command;
import com.daviipkp.smartsteve.services.CommandRegistry;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class SystemShutdownCommand extends Command  {

    @Override
    public void execute() {

        try {
            ProcessBuilder pb = new ProcessBuilder("shutdown", "/s", "/f", "/t", getArguments()[0]);

            pb.start();

        } catch (IOException e) {
            handleError(e);
        }
    }

    @Override
    public void callback() {

    }

    @Override
    public void executeSupCallback() {

    }


    @Override
    public String getID() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return "Shuts down the system. Argument of time can be used (in seconds). Example: " + CommandRegistry.getExampleUsage(getID(), "120");
    }
}
