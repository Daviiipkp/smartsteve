package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.TriggeredCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartsteve.Instance.CommandE;
import com.daviipkp.smartsteve.services.CommandRegistry;
import org.springframework.stereotype.Component;

@CommandDescription(value = "Use to check for user requests regarded to past interactions.", possibleArguments = "Any request, saved info or idea that might have been saved.")
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
