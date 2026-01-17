package com.daviipkp.smartsteve.services;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.smartsteve.Constants;
import com.daviipkp.smartsteve.Instance.ChatMessage;
import com.daviipkp.smartsteve.Instance.Protocol;
import com.daviipkp.smartsteve.prompt.Prompt;
import com.daviipkp.smartsteve.repository.ChatRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
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

        CompletableFuture<ChatMessage> fResponse = CompletableFuture.supplyAsync(() -> {
            return llmS.finalCallModel(Prompt.getDefaultPrompt(userPrompt), userPrompt);
        });

        CompletableFuture.allOf(fResponse).join();


        ChatMessage cResponse = fResponse.get();

        chatRepo.save(cResponse);
        if(Constants.DATABASE_SAVING_DEBUG) {
            SteveCommandLib.systemPrint(">> Memória salva no banco H2: " + cResponse);
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
        if(Constants.MEMORY_DEBUG) {
            SteveCommandLib.systemPrint("Trying to get Memory Consult");
        }

        for (Document doc : r) {
            String c = doc.getContent();
            Map<String, Object> m = doc.getMetadata();
            sb.append("- ").append(c).append("\n");
            if(Constants.MEMORY_DEBUG) {
                SteveCommandLib.systemPrint("Memory added: " + c);
            }
        }


        return sb.toString();

    }

    public Map<Protocol, String> getProtocols(int top, String... query) {
        Map<Protocol, String> toReturn = new HashMap<>();
        if(query.length == 0 || (query.length == 1 && query[0].equals(""))) {
            return toReturn;
        }
        VectorStore vectorStore = SpringContext.getBean(VectorStore.class);
        String q = String.join(" ", query);
        SearchRequest request = SearchRequest.query(q)
                .withTopK(top)
                .withSimilarityThreshold(0.5);


        List<Document> r;

        try {
            r = vectorStore.similaritySearch(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        if(Constants.MEMORY_DEBUG) {
            SteveCommandLib.systemPrint("Trying to get Similar protocol");
        }


        for (Document doc : r) {
            String c = doc.getContent();
            Map<String, Object> m = doc.getMetadata();
            try {
                String objType = (String)m.get("type");
                if(objType.equals("protocol")) {
                    toReturn.put(SteveJsoning.parse(c, Protocol.class), doc.getId());
                    if(Constants.MEMORY_DEBUG) {
                        SteveCommandLib.systemPrint("Similar added: " + c);
                    }
                }
            }catch (Exception e) {

            }

        }


        return toReturn;

    }

}

