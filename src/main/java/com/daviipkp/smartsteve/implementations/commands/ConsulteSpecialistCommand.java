package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartsteve.Instance.CommandE;
import com.daviipkp.smartsteve.services.CommandRegistry;
import org.springframework.stereotype.Component;

@CommandDescription(value = "Consult a specialized AI for Coding or Math problems. If this command is used, tell the user to wait.", possibleArguments = "Any coding/math related questions")
public class ConsulteSpecialistCommand extends WebRequestTriggeredCommand {

}
