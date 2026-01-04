package com.daviipkp.smartsteve.services;

import com.daviipkp.smartsteve.Instance.Command;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommandRegistry {

    @Getter
    private List<Command> commands;

    @Getter
    private List<String> commandNames;

    private final LLMService llmS;

    public CommandRegistry(ApplicationContext context, List<Command> allCommands, @Lazy LLMService llmService) {
        this.llmS = llmService;
        this.commands = allCommands;
    }

    public Command getCommand(String id) {
        for(Command c : getCommands()) {
            if(c.getID().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public List<String> getCommandNames() {
        if(commandNames == null) {
            commandNames = new ArrayList<>();
            for(Command c : getCommands()) {
                commandNames.add(c.getID() );
            }
        }
        return commandNames;
    }

    public List<String> getCommandNamesWithDesc() {
        if(commandNames == null) {
            commandNames = new ArrayList<>();
            for(Command c : getCommands()) {
                commandNames.add(c.getID() + "-" + c.getDescription());
            }
        }
        return commandNames;
    }

    public void handleNotFound(String prompt) {
        System.out.println("Handling not found command!");
        VoiceService.speak(llmS.callDefInstructedModel(prompt, "THE USER MADE THIS PROMPT BUT THE COMMAND HE ASKED WAS NOT FOUND. EXPLAIN IT TO HIM.", false).getSteveResponse(), ()->{});
    }

    public static String getExampleUsage(String command_id, String... argument) {
        if(argument.length > 0) {
            return command_id + "___" + String.join("___", argument);
        }else{
            return command_id;
        }
    }


}