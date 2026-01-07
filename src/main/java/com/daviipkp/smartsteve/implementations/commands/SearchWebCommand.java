package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveJsoning.annotations.CommandDescription;

@CommandDescription(value = "Command used to search anything in the web. User inputs, recent happenings. Anything that might be in the internet.",
        possibleArguments = "search: <String>",
        exampleUsage = "search: Who won the latest Soccer Game in Brazil?")
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
