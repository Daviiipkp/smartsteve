package com.daviipkp.smartstevex.services;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.Command;
import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.smartstevex.Configuration;
import com.daviipkp.smartstevex.Instance.ChatMessage;
import com.daviipkp.smartstevex.Instance.SteveResponse;
import com.daviipkp.smartstevex.Utils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class LLMService {

    private static final String defaultProvider = Configuration.LLM_PROVIDER;
    private static final String defaultModel = Configuration.LLM_MODEL_NAME;
    private static final String apiKey = Configuration.LLM_API_KEY;

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private long lastTick = 0;

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

        if(Configuration.FINAL_PROMPT_DEBUG) {
            SteveCommandLib.systemPrint("Final prompt: \n" + jsonBody);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(defaultProvider))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        long time = System.currentTimeMillis();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if(Configuration.PROMPT_LATENCY_DEBUG) {
            SteveCommandLib.systemPrint("Latency: " + (System.currentTimeMillis() - time) + "ms");
        }

        return response;
    }


    public ChatMessage finalCallModel(String fullPromptText, String userPrompt) {
        try {
            HttpResponse<String> response = sendRequest(fullPromptText);

            if (response.statusCode() != 200) {
                System.err.println("Error: \n" + response.statusCode() + " Body: \n" + response.body());
                return null;
            }

            processCommands(response.body());

            return new ChatMessage(userPrompt, SteveJsoning.valueAtPath("/choices/0/message/content", response.body()));

        } catch (Exception e) {
            System.out.println("Exception caught with message: " + e.getMessage());
        }
        return null;
    }

    public void finalCallModel(String fullPromptText) {
        try {
            HttpResponse<String> response = sendRequest(fullPromptText);

            if (response.statusCode() != 200) {
                System.err.println("Error: " + response.statusCode());
                return;
            }

            if(Configuration.STEVE_RESPONSE_DEBUG) {
                SteveCommandLib.systemPrint("Steve's raw response: " + response.body());
            }

            processCommands(response.body());

        } catch (Exception e) {
            System.out.println("Exception caught with message: " + e.getMessage());
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
                                }else{
                                    throw new IllegalArgumentException("You must not use non-primitive types as command arguments (with @Describe annotations)");
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
            System.err.println("Error during command processing: " + e.getMessage());
        }
    }
}