package com.daviipkp.smartsteve.services;

import com.daviipkp.smartsteve.Constants;
import com.daviipkp.smartsteve.Instance.Command;
import com.daviipkp.smartsteve.Instance.ChatMessage;
import com.daviipkp.smartsteve.repository.ChatRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class DualBrainService {



    private final RestClient restClient;
    private final ChatRepository chatRepo;

    private final VoiceService voiceService;
    private final EarService earService;
    private final SearchService searchService;
    private final CommandRegistry cmdRegistry;
    private final LLMService llmS;
    @Getter
    @Setter
    private static boolean voiceTyping = false;

    @Value("${google.api.key}")
    private String googleApiKey;



    public DualBrainService(ChatRepository arg0, VoiceService voiceService, @Lazy EarService earService, LLMService llmservice, SearchService searchService,  CommandRegistry commandRegistry) {
        this.searchService = searchService;
        this.restClient = RestClient.create();
        this.chatRepo = arg0;
        this.earService = earService;
        this.voiceService = voiceService;
        this.cmdRegistry = commandRegistry;
        this.llmS = llmservice;
    }

    public String processCommand(String userPrompt) throws ExecutionException, InterruptedException {
        String context = getContext();

        CompletableFuture<ChatMessage> fResponse = CompletableFuture.supplyAsync(() -> {
            return llmS.callDefContextedModel(userPrompt, context);
        });

        CompletableFuture.allOf(fResponse).join();


        ChatMessage cResponse = fResponse.get();
        String response = cResponse.getSteveResponse();
        String cmd = cResponse.getCommand();


        if(Constants.DEBUG) {
            System.out.println("============== NOVA REQUISIÇÃO ==============");
            System.out.println("user: " + userPrompt);
            System.out.println(">> Response: " + response);
            System.out.println(">> Command: " + cmd);
            System.out.println(">> Context: " + cResponse.getContext());


        }


        ChatMessage chatMessage = new ChatMessage(userPrompt, response, context, cmd);
        chatRepo.save(chatMessage);
        if(Constants.DEBUG) {
            System.out.println(">> Memória salva no banco H2.");
        }
        earService.stopListening();
        VoiceService.speak(response, () -> {
            earService.resumeListening();
            if(cmdRegistry.getCommandNames().contains(cmd)) {
                cmdRegistry.getCommands().get(cmdRegistry.getCommandNames().indexOf(cmd)).execute();
            }
        });

        return response;
    }

    private String getContext() {
        List<ChatMessage> messages = chatRepo.findTop10ByOrderByTimestampDesc();

        Collections.reverse(messages);

        if(messages.isEmpty()) {
            return "No context";
        }

        return messages.stream()
                .map(m -> "User: " + m.getUserPrompt()
                        + "\nSteve: " + m.getSteveResponse()
                        + "\nContext: " + m.getContext()
                        + "\nTimestamp: " + m.getTimestamp() +
                        (m.getCommand().equals("null")?"":"\nCommand: "+m.getCommand()))
                .collect(Collectors.joining("\n---\n"));
    }



//    private String detectIntent(String userPrompt) {
//        try {
//            String url = "http://localhost:11434/api/generate";
//
//            var body = Map.of(
//                    "model", "qwen3-coder:480b-cloud",
//                    "prompt", String.format(Constants.INTENT_PROMPT, Command.getCommandNames()) + " \"" + userPrompt + "\"",
//                    "stream", false,
//                    "options", Map.of()
//            );
//
//            String jsonResponse = restClient.post()
//                    .uri(url)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(body)
//                    .retrieve()
//                    .body(String.class);
//
//            return getOllamaTextFromJson(jsonResponse).trim();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "CHAT_NORMAL";
//        }
//    }



}

