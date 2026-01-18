package com.daviipkp.smartstevex.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.QueuedCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;
import com.daviipkp.smartstevex.services.VoiceService;

@CommandDescription(value = "Use to talk anything that you want. Any message as argument of this command will be spoke directly to the user. Volume range is 0 to 100, where 100 is a scream and 0 is inaudible. 30 is normal voice.")
public class TalkCommand extends QueuedCommand {

    @Describe
    private String message;

    @Describe
    private float volume;

    private boolean audioCompleted = false;

    @Override
    public void start() {
        super.start();
        VoiceService.setVolume((volume/100));
        VoiceService.speak(message, () -> {this.audioCompleted = true;});
    }

    @Override
    public void handleError(Exception e) {}

    @Override
    public void execute(long delta) {
        super.execute(delta);
        if(audioCompleted) {
            finish();
        }
    }


}
