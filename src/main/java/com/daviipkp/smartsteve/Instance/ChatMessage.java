package com.daviipkp.smartsteve.Instance;


import com.daviipkp.smartsteve.Utils;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.daviipkp.smartsteve.Utils.extractField;

@Entity
@Data
@NoArgsConstructor
public class ChatMessage {
    //Transform all this with embedding models!
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    @Lob
    private String userPrompt;

    @Lob
    private String steveResponse;

    @Lob
    private String context;

    @Lob
    private String command;

    public ChatMessage(String userPrompt, String steveResponse, String context) {
        this.userPrompt = userPrompt;
        this.steveResponse = steveResponse;
        this.context = context;
        this.command = "null";
        this.timestamp = LocalDateTime.now();
    }

    public ChatMessage(String userPrompt, String steveResponse, String context, String command) {
        this.userPrompt = userPrompt;
        this.steveResponse = steveResponse;
        this.context = context;
        this.command = command;
        this.timestamp = LocalDateTime.now();
    }

    public static ChatMessage fromJson(String rawJson, String originalUserPrompt) {
        String innerJson = Utils.extractInnerJson(rawJson);

        String speech = extractField(innerJson, "speech");
        String memory = extractField(innerJson, "memory");
        String action = extractField(innerJson, "action");

        String finalSpeech = (speech == null || speech.equalsIgnoreCase("null")) ? "" : speech;

        String finalContext = (memory == null || memory.equalsIgnoreCase("null")) ? "" : memory;

        String finalCommand = (action == null || action.equalsIgnoreCase("null")) ? "" : action;

        return new ChatMessage(originalUserPrompt, finalSpeech, finalContext, finalCommand);
    }


}
