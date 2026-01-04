package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.smartsteve.Instance.Command;
import com.daviipkp.smartsteve.services.CommandRegistry;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConsultCommand extends Command {
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
        return "Use to check for user requests regarded to past interactions. Example usage: " + CommandRegistry.getExampleUsage(getID(), "local beer business idea");
    }
}
