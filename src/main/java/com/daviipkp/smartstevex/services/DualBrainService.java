package com.daviipkp.smartstevex.services;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.smartstevex.Configuration;
import com.daviipkp.smartstevex.Instance.ChatMessage;
import com.daviipkp.smartstevex.prompt.Prompt;
import com.daviipkp.smartstevex.repository.ChatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
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
    private final LLMService llmS;
    @Getter
    @Setter
    private static boolean voiceTyping = false;

    public DualBrainService(ChatRepository arg0, LLMService llmservice) {
        this.restClient = RestClient.create();
        this.chatRepo = arg0;
        this.llmS = llmservice;
        SteveCommandLib.debug(true);
    }

    public String processUserPrompt(String userPrompt) throws ExecutionException, InterruptedException {
        CompletableFuture<ChatMessage> fResponse = CompletableFuture.supplyAsync(() -> llmS.finalCallModel(Prompt.getDefaultPrompt(userPrompt), userPrompt));

        CompletableFuture.allOf(fResponse).join();

        ChatMessage cResponse = fResponse.get();
        chatRepo.save(cResponse);
        if(Configuration.DATABASE_SAVING_DEBUG) {
            SteveCommandLib.systemPrint(">>> Prompt memory saved on database: \n" + cResponse);
        }
        return cResponse.getSteveResponse();
    }

    public String getContext() {
        List<ChatMessage> messages = chatRepo.findTop3ByOrderByTimestampDesc();
        Collections.reverse(messages);
        if(messages.isEmpty()) {
            return "";
        }

        return messages.stream()
                .map(m -> {
                    try {
                        return "User: " + m.getUserPrompt()
                                + "\nSteve memory: " + SteveJsoning.valueAtPath("/memory", m.getSteveResponse())
                                + "\nTimestamp: " + m.getTimestamp();
                    } catch (JsonProcessingException e) {
                        return "";
                    }
                })
                .collect(Collectors.joining("\n---\n"));
    }

    public String getMemoryConsult(String query) {
        StringBuilder sb = new StringBuilder();
        VectorStore vectorStore = SpringContext.getBean(VectorStore.class);

        SearchRequest request = SearchRequest.query(query)
                .withTopK(5)
                .withSimilarityThreshold(0.5);
        List<Document> r = vectorStore.similaritySearch(request);
        if(Configuration.MEMORY_DEBUG) {
            SteveCommandLib.systemPrint("Trying to get Memory Consult");
        }
        for (Document doc : r) {
            String c = doc.getContent();
            Map<String, Object> m = doc.getMetadata();
            sb.append("- ").append(c).append("\n");
            if(Configuration.MEMORY_DEBUG) {
                SteveCommandLib.systemPrint("Memory added to prompt: " + c);
            }
        }
        return sb.toString();

    }

}

