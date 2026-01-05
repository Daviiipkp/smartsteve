package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;

@CommandDescription(value = "Activates Inspection Mode.", possibleArguments = "")
public class TurnOnInspectionModeCommand extends InstantCommand {
    public TurnOnInspectionModeCommand() {
        setCommand(new Runnable() {
            @Override
            public void run() {
                SteveCommandLib.debug(true);
            }
        });
    }
}
