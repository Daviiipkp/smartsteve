package com.daviipkp.smartstevex.implementations.future;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;

@CommandDescription(value = "Used to kill instantly the system without arguments.")
public class KillSwitchCommand extends InstantCommand {

    public KillSwitchCommand() {
        setCommand(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
