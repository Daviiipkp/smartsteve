package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.QueuedCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;

@CommandDescription(value = "This command goes to the queue. Use when you need a callback after other commands. You will only receive the callback if the commands are successful.", possibleArguments = {"Any system instruction to be used in the callback.","'User asked me to turn on TV. I turned it on. Send a success message.'"})
public class QueuedCallbackCommand extends QueuedCommand {
    @Override
    public void handleError(Exception e) {

    }

    @Override
    public String getDescription() {
        return "";
    }
}
