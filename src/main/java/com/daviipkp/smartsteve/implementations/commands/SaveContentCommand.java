package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.smartsteve.Instance.Command;
import com.daviipkp.smartsteve.Instance.Content;
import com.daviipkp.smartsteve.repository.ContentRepository;
import com.daviipkp.smartsteve.services.CommandRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SaveContentCommand extends Command {

    @Autowired
    ContentRepository contentRepository;

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
        return "Use to save important content on database. Anything saved here might be consulted in the future. Example usage: " + CommandRegistry.getExampleUsage(getID(), "User's birthday is June 10th");
    }
}
