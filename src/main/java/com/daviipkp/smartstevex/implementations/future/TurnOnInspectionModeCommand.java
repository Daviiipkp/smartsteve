package com.daviipkp.smartstevex.implementations.future;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;

@CommandDescription(value = "Turns on Inspection Mode.")
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
