package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartsteve.Instance.Content;
import com.daviipkp.smartsteve.repository.ContentRepository;

@CommandDescription(value = "Use to save important content on database. Anything saved here might be consulted in the future.",
        possibleArguments = "data: <String>",
        exampleUsage = "data: User mother's birthday date is November 17th")
public class SaveContentCommand extends InstantCommand {

    public SaveContentCommand(ContentRepository repo, Content content) {
        setCommand(() -> repo.save(content));
    }
}
