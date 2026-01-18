package com.daviipkp.smartstevex.implementations.commands;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;
import com.daviipkp.smartstevex.Configuration;
import com.daviipkp.smartstevex.Instance.Protocol;
import com.daviipkp.smartstevex.services.ProtocolsService;
import com.daviipkp.smartstevex.services.SpringContext;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;
import java.util.Set;

@CommandDescription(value = "Add commands to be executed in a protocol.")
public class AddCommandToProtocolCommand extends InstantCommand {

    @Describe()
    private String name;

    @Describe(description = """
            "commands": {
                    "<Command Name>": {
                        "<Argument Name>": "<Argument Value>"
                    }
                }
            """)
    private Map<String, Map<String, String>> commands;

    public AddCommandToProtocolCommand() {
        setCommand(new Runnable() {
            @Override
            public void run() {
                ProtocolsService dbs = SpringContext.getBean(ProtocolsService.class);
                VectorStore vectorStore = SpringContext.getBean(VectorStore.class);
                Map<Protocol, String> p = dbs.getProtocols(1, name);
                if(p.isEmpty()) {
                    throw new RuntimeException("No protocols found with name " + name);
                }
                Protocol first =  p.keySet().iterator().next();
                vectorStore.delete(List.of(p.get(first)));

                Set<String> newCmdNames = commands.keySet();
                for(String newCmd : newCmdNames) {
                    first.getCommands().put(newCmd, commands.get(newCmd));
                }

                Map<String, Object> d = Map.of(
                        "type", "protocol"
                );

                String content = SteveJsoning.stringify(first);

                Document doc = new Document(content, d);


                vectorStore.add(List.of(doc));

                if(Configuration.MEMORY_DEBUG) {
                    SteveCommandLib.systemPrint("Saved protocol as:\n" + content);
                }

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
