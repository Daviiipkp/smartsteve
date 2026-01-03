package com.daviipkp.smartsteve.Instance;

import com.daviipkp.smartsteve.services.DualBrainService;
import com.daviipkp.smartsteve.services.LLMService;
import com.daviipkp.smartsteve.services.VoiceService;
import implementations.KillSwitchCommand;
import implementations.PlayOnSpotifyCommand;
import implementations.SearchWebCommand;
import implementations.SystemShutdownCommand;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public abstract class Command {

    private static List<Command> commands;
    private static List<String> commandNames;
    @Getter
    @Setter
    private String[] arguments;

    @Getter
    @Setter
    private boolean autoCallback;

    @Getter
    @Setter
    private boolean ShouldUseSupCallback;

    @Getter
    @Setter
    private Supplier<Void> supCallback;

    public abstract void execute();

    public abstract void callback();

    public abstract void executeSupCallback();

    public abstract String getID();

    public abstract String getDescription(String description);

    public static List<Command> getCommands() {
        if (commands == null) {
            commands = List.of(
                    new SearchWebCommand(),
                    new SystemShutdownCommand(),
                    new KillSwitchCommand(),
                    new PlayOnSpotifyCommand()
            );
        }
        return commands;
    }

    public static List<String> getCommandNames() {
        if(commandNames == null) {
            commandNames = new ArrayList<>();
            for(Command c : getCommands()) {
                commandNames.add(c.getID());
            }
        }
        return commandNames;
    }

    public static Command getCommand(String id) {
        for(Command c :  getCommands()) {
            if(c.getID().equals(id)) {return c;}

        }return null;
    }

    public static void handleNotFound(String prompt) {
        VoiceService.speak(LLMService.callInstructedModel(prompt, "THE USER MADE THIS PROMPT BUT THE COMMAND HE ASKED WAS NOT FOUND. EXPLAIN IT TO HIM.", false), ()->{});
    }

}