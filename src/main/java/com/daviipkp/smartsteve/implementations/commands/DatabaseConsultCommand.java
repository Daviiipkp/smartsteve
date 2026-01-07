package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveJsoning.annotations.CommandDescription;

@CommandDescription(value = "Use to check for user requests regarded to past interactions. Any request, saved info or idea that might have been saved.",
        possibleArguments = "request: <String>",
        exampleUsage = "request: Business Idea from User about selling software")
public class DatabaseConsultCommand extends WebRequestTriggeredCommand {

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
