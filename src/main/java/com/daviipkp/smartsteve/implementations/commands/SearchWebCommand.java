package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.TriggeredCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartsteve.Instance.CommandE;
import com.daviipkp.smartsteve.services.CommandRegistry;
import lombok.Getter;
import org.springframework.stereotype.Component;

@CommandDescription(value = "Command used to search anything in the web.", possibleArguments = "User inputs, recent happenings. Anything that might be in the internet.")
public class SearchWebCommand extends WebRequestTriggeredCommand {


    @Override
    public void start() {
        super.start();
        //Web request logic
    }

    @Override
    public boolean checkTrigger() {
        //Web request returned logic (if returned, AI Callback)
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
