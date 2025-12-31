package com.daviipkp.smartsteve.services;

import com.daviipkp.smartsteve.Constants;
import com.daviipkp.smartsteve.Instance.Command;
import com.daviipkp.smartsteve.model.ChatMessage;
import com.daviipkp.smartsteve.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.awt.*;
import java.io.IOException;
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

    private final List<Command> commands = new ArrayList<>();
    private final VoiceService voiceService;
    private final EarService earService;
    List<String> commandNames;

    @Value("${google.api.key}")
    private String googleApiKey;

    public DualBrainService(ChatRepository arg0, VoiceService voiceService, @Lazy EarService earService) {
        this.restClient = RestClient.create();
        this.chatRepo = arg0;
        this.earService = earService;
        commands.add(new Command(() -> {
            try {
                Desktop.getDesktop().browse(new URI("https://youtube.com"));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
            }, "OPEN_YOUTUBE_WEBSITE"));
        commandNames = commands.stream()
                .map(Command::getCMD_ID)
                .toList();
        this.voiceService = voiceService;
    }

    public String processCommand(String userPrompt) throws ExecutionException, InterruptedException {
        String context = getContext();

        CompletableFuture<String> futureIntent = CompletableFuture.supplyAsync(() -> {
            return detectIntent(userPrompt);
        });

        CompletableFuture<String> futureLocalResponse = CompletableFuture.supplyAsync(() -> {
            return callOllamaLocal(userPrompt);
        });

        CompletableFuture.allOf(futureIntent, futureLocalResponse).join();

        String intent = futureIntent.get();
        if(commandNames.contains(intent)) {
            commands.get(commandNames.indexOf(intent)).execute();
        }
        String localResponse = futureLocalResponse.get();

        if(Constants.DEBUG) {
            System.out.println("============== NOVA REQUISIÇÃO ==============");
            System.out.println("Usuario: " + userPrompt);
            System.out.println(">> Local: " + localResponse);

        }
        String cloudResponse = callGeminiCloud(userPrompt, localResponse, context);

        if(Constants.DEBUG) {
            System.out.println(">> Nuvem: " + cloudResponse);
        }
        if(Constants.DEBUG) {
            System.out.println(">> Commando requisitado! Resposta: " + intent );
        }

        ChatMessage chatMessage = new ChatMessage(userPrompt, localResponse + cloudResponse);
        //chatRepo.save(chatMessage);
        if(Constants.DEBUG) {
            System.out.println(">> Memória salva no banco H2.");
        }
        earService.stopListening();
        voiceService.speak(localResponse, () -> {
            earService.resumeListening();
        });

        return localResponse + " " + cloudResponse;
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

    private String callOllamaLocal(String userPrompt) {
        try {

            long l = System.currentTimeMillis();
            String url = "http://localhost:11434/api/generate";
            var body = Map.of(
                    "model", "qwen3-coder:480b-cloud",
                    "prompt", String.format(Constants.LOCAL_PROMPT + userPrompt, commandNames),
                    "stream", false
            );

            String jsonResponse = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return getOllamaTextFromJson(jsonResponse);
        }catch(Exception e) {e.printStackTrace();return "";}

    }

    private String callGeminiCloud(String userPrompt, String localResponse, String context) {
//        try {
//            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + googleApiKey;
//            String finalPrompt = String.format(Constants.REMOTE_PROMPT, userPrompt, localResponse,  context);
//            var body = Map.of(
//                    "contents", List.of(
//                            Map.of("parts", List.of(
//                                    Map.of("text", finalPrompt)
//                            ))
//                    )
//            );
//
//            String jsonResponse = restClient.post()
//                    .uri(url)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(body)
//                    .retrieve()
//                    .body(String.class);
//            return getGeminiTextFromJson(jsonResponse);
//        }catch(Exception e) {e.printStackTrace();return "";}
        return "";
    }

    private String getGeminiTextFromJson(String json) {
        if (json == null) return "";

        String marcador = "\"text\": \"";
        int indexMarcador = json.lastIndexOf(marcador);

        if (indexMarcador == -1) {
            return "";
        }

        int start = indexMarcador + marcador.length();
        int end = json.indexOf("\"", start);

        return json.substring(start, end).replace("\\n", " ");
    }

    private String getOllamaTextFromJson(String json) {

        if (json == null) return "";
        int start = json.indexOf("\"response\":\"") + 12;

        int end = json.indexOf("\",\"done\"");

        if (start < 12 || end == -1) return "Error parser Ollama: " + json;
        return json.substring(start, end).replace("\\n", " ");
    }

    private String detectIntent(String userPrompt) {
        try {
            String url = "http://localhost:11434/api/generate";

            var body = Map.of(
                    "model", "qwen3-coder:480b-cloud",
                    "prompt", String.format(Constants.INTENT_PROMPT, commandNames) + " \"" + userPrompt + "\"",
                    "stream", false,
                    "options", Map.of("brightness", 100.0f)
            );

            String jsonResponse = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return getOllamaTextFromJson(jsonResponse).trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "CHAT_NORMAL";
        }
    }

}

