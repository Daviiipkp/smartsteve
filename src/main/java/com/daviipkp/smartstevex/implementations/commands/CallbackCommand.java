package com.daviipkp.smartstevex.implementations.commands;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.Command;
import com.daviipkp.SteveCommandLib.instance.QueuedCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;
import com.daviipkp.smartstevex.prompt.Prompt;
import com.daviipkp.smartstevex.services.LLMService;
import com.daviipkp.smartstevex.services.SpringContext;

@CommandDescription(value = "Use when you need a callback after other commands. If you want to be called for another function after some time, use this after a WaitCommand, but remember to tell the next Callback model to not repeat what you've said, just confirm the order.")
public class CallbackCommand extends QueuedCommand {

    @Describe
    private String instructions;
    @Describe
    private String context;

    @Override
    public void handleError(Exception e) {

    }

    @Override
    public void execute(long delta) {
        super.execute(delta);
        LLMService llm = SpringContext.getBean(LLMService.class);
        llm.finalCallModel(Prompt.getCallBackPrompt(instructions, context));
        finish();
    }

    public static void asError(Class<? extends Command> command, Exception exception) throws InstantiationException, IllegalAccessException {
        SteveCommandLib.addCommand(CallbackCommand.class.newInstance().setInstructions("User asked for " + command.getSimpleName() + " but it failed with exception " + exception.getClass().getSimpleName() + " and message " + exception.getMessage()).build());
    }

    public CallbackCommand setInstructions(String system_instructions) {
        this.instructions = system_instructions;
        return this;
    }

    public CallbackCommand build() {
        return this;
    }

}
