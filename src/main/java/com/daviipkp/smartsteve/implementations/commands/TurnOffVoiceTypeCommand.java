package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.smartsteve.services.EarService;

public class TurnOffVoiceTypeCommand extends InstantCommand {

    public TurnOffVoiceTypeCommand(EarService eService) {
        setCommand(new Runnable() {
            @Override
            public void run() {
                eService.stopVoiceTyping();
            }
        });
    }
}
