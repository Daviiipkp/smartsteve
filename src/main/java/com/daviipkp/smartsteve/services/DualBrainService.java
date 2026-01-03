package com.daviipkp.smartsteve.services;

import com.daviipkp.smartsteve.Constants;
import com.daviipkp.smartsteve.Instance.Command;
import com.daviipkp.smartsteve.model.ChatMessage;
import com.daviipkp.smartsteve.repository.ChatRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
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
    @Getter
    @Setter
    private static boolean voiceTyping = false;

    private static String command_arg = "";
    private static String lastPrompt;

    @Value("${google.api.key}")
    private String googleApiKey;



    public DualBrainService(ChatRepository arg0, VoiceService voiceService, @Lazy EarService earService, SearchService searchService) {
        this.searchService = searchService;
        this.restClient = RestClient.create();
        this.chatRepo = arg0;
        this.earService = earService;
//        commands.add(new Command(() -> {
//            try {
//                Desktop.getDesktop().browse(new URI("https://youtube.com"));
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//            return true;
//            }, "OPEN_YOUTUBE_WEBSITE"));
//        commands.add(new Command(() -> {
//            setVoiceTyping(true);
//            return true;
//        }, "START_VOICE_TYPING"));
//        commands.add(new Command(() -> {
//            setVoiceTyping(false);
//            return true;
//        }, "STOP_VOICE_TYPING"));
//        commands.add(new Command(() -> {
//            System.out.println("Played " + command_arg);
//            return true;
//        }, "PLAY_ON_SPOTIFY"));
//        commands.add(new Command(() -> {
//            earService.stopListening();
//            voiceService.speak(callOllamaLocal(String.format("Those were the results of a search made by the user: %s --- Please answer the results in a polite way. Use user prompt was: %s", searchService.searchAndSummarize(command_arg), lastPrompt), true), earService::resumeListening);
//
//            return true;
//        }, "SEARCH_WEB"));

        this.voiceService = voiceService;
    }

    public String processCommand(String userPrompt) throws ExecutionException, InterruptedException {
        String context = getContext();


        CompletableFuture<String> fResponse = CompletableFuture.supplyAsync(() -> {
            return LLMService.callModel(userPrompt);
        });

        CompletableFuture.allOf(fResponse).join();
        String response = fResponse.get();
        List<String> a = Arrays.asList(response.split("___SEPARATOR___"));
        List<String> b = new ArrayList<>();
        for(String s : a) {
            if(a.indexOf(s) == 0) {
                continue;
            }
            b.add(s);

        }



        command_arg = String.join("", b);


        if(Constants.DEBUG) {
            System.out.println("============== NOVA REQUISIÇÃO ==============");
            System.out.println("user: " + userPrompt);
            System.out.println(">> Response: " + response);

        }
        if(Constants.DEBUG) {
            System.out.println(">> Commando requisitado! " + command_arg );
        }

        ChatMessage chatMessage = new ChatMessage(userPrompt, response);
        //chatRepo.save(chatMessage);
        if(Constants.DEBUG) {
            System.out.println(">> Memória salva no banco H2.");
        }
        earService.stopListening();
        String firstPartOfIntent = a.get(0).trim();
        voiceService.speak(response, () -> {
            earService.resumeListening();
            if(Command.getCommandNames().contains(firstPartOfIntent)) {
                Command.getCommands().get(Command.getCommandNames().indexOf(firstPartOfIntent)).execute();
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
                .map(m -> "User: " + m.getUserPrompt() + "\nSteve: " + m.getSteveResponse())
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

