package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.smartsteve.Instance.Command;
import org.springframework.stereotype.Component;

@Component
public class TurnOffVoiceTypeCommand extends Command {
    @Override
    public void execute() {

    }

    @Override
    public void callback() {

    }

    @Override
    public void executeSupCallback() {

    }

    @Override
    public String getID() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Turns off Voice Typing.";
    }
}
