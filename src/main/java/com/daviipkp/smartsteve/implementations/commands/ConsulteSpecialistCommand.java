package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.smartsteve.Instance.Command;
import com.daviipkp.smartsteve.services.CommandRegistry;
import org.springframework.stereotype.Component;

@Component
public class ConsulteSpecialistCommand extends Command {
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
        return "Consult a specialized AI for Coding or Math problems. If this command is used, tell the user to wait. Example usage: " +  CommandRegistry.getExampleUsage(getID(), "How do I set up a Java Spring Auth Configuration?");
    }
}
