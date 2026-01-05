package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;

@CommandDescription(value = "Use to talk anything that you want.", possibleArguments = "Any message as argument of this command will be spoke directly to the user.")
public class InstantTalkCommand extends InstantCommand {

    public InstantTalkCommand() {
        setCommand(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
