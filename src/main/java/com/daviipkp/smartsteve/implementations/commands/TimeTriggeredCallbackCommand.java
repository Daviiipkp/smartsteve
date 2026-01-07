package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.TriggeredCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartsteve.repository.TriggersRepository;

import java.time.LocalDateTime;

@CommandDescription(value = "Used to have a callback when a certain time comes. Useful if the user asks to be warned in specific times.",
        possibleArguments = "time: <Formatted Date/Time>",
        exampleUsage = "time: 1/1/2026 3:00PM")
public class TimeTriggeredCallbackCommand extends TriggeredCommand {

    private final TriggersRepository triggersRepository;

    private final LocalDateTime triggerTime;

    public TimeTriggeredCallbackCommand(TriggersRepository triggersRepository, LocalDateTime time) {
        this.triggersRepository = triggersRepository;
        triggerTime = time;
    }

    @Override
    public boolean checkTrigger() {
        return triggerTime.isAfter(LocalDateTime.now());
    }

    @Override
    public void handleError(Exception e) {

    }

    @Override
    public String getDescription() {
        return this.getClass().getAnnotation(CommandDescription.class).value();
    }

    @Override
    public void execute(long delta) {

    }


}
