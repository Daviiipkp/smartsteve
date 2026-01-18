package com.daviipkp.smartstevex.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;
import com.daviipkp.smartstevex.Instance.Protocol;
import com.daviipkp.smartstevex.services.ProtocolsService;
import com.daviipkp.smartstevex.services.SpringContext;

import java.util.Map;

@CommandDescription(value = "Execute a protocol.")
public class ExecuteProtocolCommand extends InstantCommand {

    @Describe
    private String name;

    public ExecuteProtocolCommand() {
        setCommand(new Runnable() {
            @Override
            public void run() {
                Map<Protocol, String> protocols = SpringContext.getBean(ProtocolsService.class).getProtocols(1, name);
                if(protocols.isEmpty()) {
                    throw new RuntimeException("No protocols found with name " + name);
                }
                protocols.keySet().forEach(protocol -> {protocol.execute();});

            }
        });
    }

    @Override
    public void handleError(Exception e) {
        super.handleError(e);
        try {
            CallbackCommand.asError(this.getClass(), e);
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
