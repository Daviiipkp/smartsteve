package com.daviipkp.smartstevex.implementations.future;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartstevex.services.EarService;

@CommandDescription(value = "Turns off voice typing.")
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
