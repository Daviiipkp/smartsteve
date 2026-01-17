package com.daviipkp.smartsteve.services;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.Command;
import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.smartsteve.Constants;
import com.daviipkp.smartsteve.Instance.ChatMessage;
import com.daviipkp.smartsteve.Instance.SteveResponse;
import com.daviipkp.smartsteve.Utils;
import com.daviipkp.smartsteve.implementations.commands.TalkCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class LLMService {

    private static final String defaultProvider = "https://api.groq.com/openai/v1/chat/completions";
    private static final String defaultModel = "openai/gpt-oss-120b";
    private static final String apiKeyName = "${groq.api.key}";
    static String apiKey;

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private long lastTick = 0;

    @Value(apiKeyName)
    public void setApiKey(String value) {
        apiKey = value;
    }

    public static void warmUp() {
        new Thread(() -> {
            try {
                String jsonBody = """
                {
                    "model": "llama-3.1-8b-instant",
                    "messages": [{"role": "user", "content": "hi"}],
                    "max_tokens": 1
                }
                """;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(defaultProvider))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();
                HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            } catch (Exception e) {
                System.err.println("error on Warm-up: " + e.getMessage());
            }
        }).start();
    }

    @Scheduled(fixedRate = 50)
    private void tick() {
        if(lastTick == 0) {
            lastTick = System.currentTimeMillis();
        }
        SteveCommandLib.tick(System.currentTimeMillis() - lastTick);
        lastTick = System.currentTimeMillis();
    }

    private HttpResponse<String> sendRequest(String fullPromptText) throws Exception {
        String escapedPrompt = fullPromptText
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");

        String jsonBody = """
        {
            "model": "%s",
            "messages": [
                {
                    "role": "user", 
                    "content": "%s"
                }
            ]
        }
        """.formatted(defaultModel, escapedPrompt);

        if(Constants.FINAL_PROMPT_DEBUG) {
            SteveCommandLib.systemPrint("Prompt sent length: " + fullPromptText.length());
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(defaultProvider))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        long time = System.currentTimeMillis();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        SteveCommandLib.systemPrint("Latency: " + (System.currentTimeMillis() - time) + "ms");

        return response;
    }

    private String extractToken(String jsonPart) {
        try {
            int contentIndex = jsonPart.indexOf("\"content\":\"");
            if (contentIndex == -1) return null;

            int start = contentIndex + 11;
            int end = jsonPart.indexOf("\"", start);
            while (jsonPart.charAt(end - 1) == '\\') {
                end = jsonPart.indexOf("\"", end + 1);
            }

            String rawToken = jsonPart.substring(start, end);
            return rawToken.replace("\\n", "\n").replace("\\\"", "\"");

        } catch (Exception e) {
            return null;
        }
    }

    public ChatMessage finalCallModel(String fullPromptText, String userPrompt) {
        try {
            HttpResponse<String> response = sendRequest(fullPromptText);

            if (response.statusCode() != 200) {
                System.err.println("Groq Error: " + response.statusCode() + " Body: " + response.body());
                return null;
            }

            processCommands(response.body());

            return new ChatMessage(userPrompt, SteveJsoning.valueAtPath("/choices/0/message/content", response.body()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void finalCallModel(String fullPromptText) {
        try {
            HttpResponse<String> response = sendRequest(fullPromptText);

            if (response.statusCode() != 200) {
                System.err.println("Groq Error: " + response.statusCode());
                return;
            }

            if(Constants.STEVE_RESPONSE_DEBUG) {
                SteveCommandLib.systemPrint("STEVE RAW: " + response.body());
            }

            processCommands(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processCommands(String responseBody) {
        try {
            String content = SteveJsoning.valueAtPath("/choices/0/message/content", responseBody);
            SteveResponse s = SteveJsoning.parse(content, SteveResponse.class);

            if(s.action != null) {
                for(String cmd : s.action.keySet()) {
                    try {
                        Command command = Utils.getCommandByName(cmd);
                        if (command == null) {
                            System.err.println("Command not found: " + cmd);
                            continue;
                        }

                        for(String argName : s.action.get(cmd).keySet()) {
                            try {
                                Field field = command.getClass().getDeclaredField(argName);
                                field.setAccessible(true);
                                String rawValue = s.action.get(cmd).get(argName);

                                Class<?> type = field.getType();
                                if(type.isAssignableFrom(String.class)) {
                                    field.set(command, rawValue);
                                } else if(type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)) {
                                    field.set(command, Integer.parseInt(rawValue));
                                } else if(type.isAssignableFrom(float.class) || type.isAssignableFrom(Float.class)) {
                                    field.set(command, Float.parseFloat(rawValue));
                                } else if(type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)) {
                                    field.set(command, Double.parseDouble(rawValue));
                                } else if(type.isAssignableFrom(boolean.class) || type.isAssignableFrom(Boolean.class)) {
                                    field.set(command, Boolean.parseBoolean(rawValue));
                                } else if(type.isAssignableFrom(long.class) || type.isAssignableFrom(Long.class)) {
                                    field.set(command, Long.parseLong(rawValue));
                                }
                            } catch (NoSuchFieldException e) {
                                System.err.println("Field '" + argName + "' not found in command " + cmd);
                            }
                        }
                        SteveCommandLib.addCommand(command);
                    } catch (Exception e) {
                        System.err.println("Error instantiating command " + cmd + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}