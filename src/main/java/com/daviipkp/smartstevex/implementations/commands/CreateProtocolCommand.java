package com.daviipkp.smartstevex.implementations.commands;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;
import com.daviipkp.smartstevex.Configuration;
import com.daviipkp.smartstevex.Instance.Protocol;
import com.daviipkp.smartstevex.services.SpringContext;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;

@CommandDescription(value = "Create a new protocol with a specific name.")
public class CreateProtocolCommand extends InstantCommand {

    @Describe
    private String name;

    public CreateProtocolCommand() {
        setCommand(new Runnable() {
            @Override
            public void run() {
                VectorStore vectorStore = SpringContext.getBean(VectorStore.class);

                String content = SteveJsoning.stringify(new Protocol(name));

                if (content == null || content.trim().isEmpty()) {
                    SteveCommandLib.systemPrint("no content");
                    return;
                }

                Map<String, Object> d = Map.of(
                        "type", "protocol"
                );

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
