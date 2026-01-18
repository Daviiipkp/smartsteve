package com.daviipkp.smartstevex.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.TriggeredCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;
import com.daviipkp.smartstevex.prompt.Prompt;
import com.daviipkp.smartstevex.services.LLMService;
import com.daviipkp.smartstevex.services.SpringContext;

import java.time.LocalDateTime;

@CommandDescription(value = "Used to have a callback when a certain time comes. Useful if the user asks a Instant Command to happen in a specific time.")
public class TimeTriggeredCallbackCommand extends TriggeredCommand {

    @Describe(description = "<Formatted Date/Time>")
    private String time;

    @Describe
    private String instructions;

    @Describe
    private String context;

    private LocalDateTime triggerTime;

    @Override
    public boolean checkTrigger() {
        return LocalDateTime.now().isAfter(triggerTime);
    }

    @Override
    public void start() {
        super.start();
        triggerTime = LocalDateTime.parse(time);
    }

    @Override
    public void handleError(Exception e) {

    }

    @Override
    public void execute(long delta) {
        SpringContext.getBean(LLMService.class).finalCallModel(Prompt.getCallBackPrompt(instructions, context));
        finish();
    }


}
