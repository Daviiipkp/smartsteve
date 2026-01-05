package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.TriggeredCommand;

public class WebRequestTriggeredCommand extends TriggeredCommand {
    @Override
    public boolean checkTrigger() {
        return false;
    }

    @Override
    public void handleError(Exception e) {

    }

    @Override
    public String getDescription() {
        return "";
    }
}
