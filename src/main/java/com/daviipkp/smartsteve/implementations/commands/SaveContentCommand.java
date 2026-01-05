package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartsteve.Instance.CommandE;
import com.daviipkp.smartsteve.Instance.Content;
import com.daviipkp.smartsteve.repository.ContentRepository;
import com.daviipkp.smartsteve.services.CommandRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@CommandDescription(value = "Use to save important content on database. Anything saved here might be consulted in the future.", possibleArguments = "Important data about the user or conversations that he might ask about again.")
public class SaveContentCommand extends InstantCommand {

    public SaveContentCommand(ContentRepository repo, Content content) {
        setCommand(() -> repo.save(content));
    }
}
