package com.daviipkp.smartsteve.services;

import com.daviipkp.smartsteve.Constants;
import com.daviipkp.smartsteve.Instance.Command;
import com.daviipkp.smartsteve.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LLMService {

    private static final String defaultModel = "https://ai.hackclub.com/proxy/v1/chat/completions";
    static String apiKey;

    @Value("${hcai.api.key}")
    public void setApiKey(String value) {
        apiKey = value;
    }

    private static String finalCallModel(String fullPromptText) {

        String escapedPrompt = fullPromptText
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");

        String jsonBody = """
        {
            "model": "google/gemini-3-flash-preview",
            "messages": [
                {
                    "role": "user", 
                    "content": "%s"
                }
            ]
        }
        """.formatted(escapedPrompt);

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(defaultModel))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("error: " + response.statusCode());
                System.err.println("error bodyy: " + response.body());
                return "";
            }
            String s = handleResponse(response.body());
            return s;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String callModel(String userPrompt) {
        String fullPromptText = String.format(Constants.getPrompt(true, false, false), Command.getCommandNames())
                + "\n" + userPrompt;
        return finalCallModel(fullPromptText);
    }

    public static String callInstructedModel(String userPrompt, String sysInstructions, boolean sendCommands) {
        String fullPromptText;
        if(sendCommands) {
            fullPromptText = String.format(Constants.getPrompt(sendCommands, false, true), Command.getCommandNames(), sysInstructions)
                    + "\n" + userPrompt;
        }else{
            fullPromptText = String.format(Constants.getPrompt(sendCommands, false, true), sysInstructions)
                    + "\n" + userPrompt;
        }
        return finalCallModel(fullPromptText);
    }

    public static String callContextedModel(String userPrompt, String context) {
        String fullPromptText = String.format(Constants.getPrompt(true, false, false), Command.getCommandNames(), context)
                + "\n" + userPrompt;
        return finalCallModel(fullPromptText);
    }

    public static String callModel(String userPrompt, String context, String sysInstructions) {
        String fullPromptText = String.format(Constants.getPrompt(true, true, true), Command.getCommandNames(), context, sysInstructions)
                + "\n" + userPrompt;
        return finalCallModel(fullPromptText);
    }


    private static String handleResponse(String jsonResponse) {
        try {
            Pattern contentPattern = Pattern.compile("\"content\"\\s*:\\s*\"(.*?)(?<!\\\\)\"");
            Matcher contentMatcher = contentPattern.matcher(jsonResponse);

            if (!contentMatcher.find()) {
                System.err.println("couldn't find content");
                return "";
            }

            String rawInnerJson = contentMatcher.group(1);
            String innerJson = rawInnerJson
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"");

            String status = Utils.extractJsonField(innerJson, "status");
            String action = Utils.extractJsonField(innerJson, "action");
            String speech = Utils.extractJsonField(innerJson, "speech");

            if ("IGNORE".equalsIgnoreCase(status)) {
                System.out.println("[LOG] input ignored.");
                return "";
            }
            System.out.println(action);
            if (action != null && !action.equals("null") && !action.isEmpty()) {
                if(Command.getCommandNames().contains(action)) {
                    try {
                        Command.getCommand(action).execute();
                    } catch (NullPointerException e) {
                        Command.handleNotFound(action);
                        return "";
                    }
                }
            }

            if (speech != null && !speech.equals("null") && !speech.isEmpty()) {
                System.out.println("Speech: " +speech);
                return speech;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
