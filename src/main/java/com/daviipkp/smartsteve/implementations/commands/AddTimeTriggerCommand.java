package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.smartsteve.Instance.Command;
import com.daviipkp.smartsteve.repository.TriggersRepository;
import com.daviipkp.smartsteve.implementations.triggers.TimeTrigger;
import com.daviipkp.smartsteve.services.CommandRegistry;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AddTimeTriggerCommand extends Command {

    TriggersRepository triggersRepository;

    public AddTimeTriggerCommand(TriggersRepository triggersRepository) {
        this.triggersRepository = triggersRepository;
    }

    @Override
    public void execute() {

        LocalDateTime time = LocalDateTime.parse(getArguments()[0]);
        triggersRepository.save(new TimeTrigger(getArguments()[1], time));
    }

    @Override
    public void callback() {}

    @Override
    public void executeSupCallback() {}

    @Override
    public String getID() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return "Add triggers for any time. AI will be called automatically and receive context attributed to the response when the time comes. Usage example: " + CommandRegistry.getExampleUsage(getID(), LocalDateTime.now().plusHours(2).toString());
    }
}
