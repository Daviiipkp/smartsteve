package com.daviipkp.smartstevex.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartstevex.Configuration;

import java.awt.*;
import java.io.File;

@CommandDescription(value = "Use to play the Alarm.")
public class AlarmCommand extends InstantCommand {

    public AlarmCommand() {
        setCommand(() -> {
            File m = new File(Configuration.ALARM_PATH);
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(m);
                } else {
                    System.err.println("Error trying to play Alarm. Desktop is not supported.");
                }
            } catch (Exception e) {
                System.err.println("Error trying to play Alarm: "  + e.getMessage());
            }
        });
    }

}
