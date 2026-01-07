package com.daviipkp.smartsteve.services;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.smartsteve.Constants;
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
    private final LLMService llmS;
    @Getter
    @Setter
    private static boolean voiceTyping = false;

    @Value("${google.api.key}")
    private String googleApiKey;

    public DualBrainService(ChatRepository arg0, VoiceService voiceService, @Lazy EarService earService, LLMService llmservice, SearchService searchService) {
        this.searchService = searchService;
        this.restClient = RestClient.create();
        this.chatRepo = arg0;
        this.earService = earService;
        this.voiceService = voiceService;
        this.llmS = llmservice;
        SteveCommandLib.debug(true);
    }

    public String processCommand(String userPrompt) throws ExecutionException, InterruptedException {
        String context = getContext();

        CompletableFuture<ChatMessage> fResponse = CompletableFuture.supplyAsync(() -> {
            return llmS.callDefContextedModel(userPrompt, context);
        });

        CompletableFuture.allOf(fResponse).join();


        ChatMessage cResponse = fResponse.get();


        SteveCommandLib.systemPrint("============== NOVA REQUISIÇÃO ==============");
        SteveCommandLib.systemPrint("user: " + userPrompt);
        SteveCommandLib.systemPrint(">> Response: " + cResponse.getSteveResponse());
        SteveCommandLib.systemPrint(">> Command: " + cResponse.getCommand());
        SteveCommandLib.systemPrint(">> Context: " + cResponse.getContext());

        chatRepo.save(cResponse);
        SteveCommandLib.systemPrint(">> Memória salva no banco H2: " + cResponse);

        earService.stopListening();

        return cResponse.getSteveResponse();
    }

    private String getContext() {
        List<ChatMessage> messages = chatRepo.findTop3ByOrderByTimestampDesc();

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

}

