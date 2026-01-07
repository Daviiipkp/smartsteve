package com.daviipkp.smartsteve.services;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.Command;
import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.smartsteve.Constants;
import com.daviipkp.smartsteve.Instance.ChatMessage;
import com.daviipkp.smartsteve.Instance.SteveResponse;
import com.daviipkp.smartsteve.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class LLMService {

    private static final String defaultProvider = "https://ai.hackclub.com/proxy/v1/chat/completions";
    private static final String defaultModel = "qwen/qwen3-32b";
    private static final String defaultEmbeddingModel = "openai/text-embedding-3-small";
    static String apiKey;

    @Value("${hcai.api.key}")
    public void setApiKey(String value) {
        apiKey = value;
    }

    private ChatMessage finalCallModel(String fullPromptText, String userPrompt) {
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

        try {
            long time = System.currentTimeMillis();
            SteveCommandLib.systemPrint("Prompt sent: " + fullPromptText);
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(defaultProvider))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            SteveCommandLib.systemPrint("Request time: " + (System.currentTimeMillis() - time));

            if (response.statusCode() != 200) {
                System.err.println("error: " + response.statusCode());
                System.err.println("error bodyy: " + response.body());
                return null;
            }

            SteveResponse s = SteveJsoning.parse(SteveJsoning.valueAtPath("/choices/0/message",response.body()), SteveResponse.class);


            System.out.println(s.action.toString());
            return new ChatMessage("", "", "", "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ChatMessage callDefModel(String userPrompt) {
        String fullPromptText = String.format(Constants.getDefaultPrompt(true, false, false), Utils.getCommandNamesWithDesc())
                + "\n" + userPrompt;
        return finalCallModel(fullPromptText, userPrompt);
    }

    public ChatMessage callDefInstructedModel(String userPrompt, String sysInstructions, boolean sendCommands) {
        String fullPromptText;
        if(sendCommands) {
            fullPromptText = String.format(Constants.getDefaultPrompt(sendCommands, false, true), Utils.getCommandNamesWithDesc(), sysInstructions)
                    + "\n" + userPrompt;
        }else{
            fullPromptText = String.format(Constants.getDefaultPrompt(sendCommands, false, true), sysInstructions)
                    + "\n" + userPrompt;
        }
        return finalCallModel(fullPromptText, userPrompt);
    }

    public  ChatMessage callDefContextedModel(String userPrompt, String context) {
        String fullPromptText = String.format(Constants.getDefaultPrompt(true, true, false), Utils.getCommandNamesWithDesc(), context)
                + "\n" + userPrompt;
        return finalCallModel(fullPromptText, userPrompt);
    }

    public ChatMessage callDefModel(String userPrompt, String context, String sysInstructions) {
        String fullPromptText = String.format(Constants.getDefaultPrompt(true, true, true), Utils.getCommandNamesWithDesc(), context, sysInstructions)
                + "\n" + userPrompt;
        return finalCallModel(fullPromptText, userPrompt);
    }

    public float[] callEmbeddingModel(String textToEmbed) {
        String escapedText = textToEmbed
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ");

        String jsonBody = """
            {
                "model": "%s",
                "input": "%s"
            }
            """.formatted(defaultEmbeddingModel, escapedText);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(defaultEmbeddingModel))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Embedding Error: " + response.body());
                return null;
            }

            String body = response.body();
            int start = body.indexOf("\"embedding\":") + 12;
            int end = body.indexOf("]", start) + 1;

            String vectorString = body.substring(start, end).replace("[", "").replace("]", "").trim();

            String[] parts = vectorString.split(",");
            float[] vector = new float[parts.length];
            for (int i = 0; i < parts.length; i++) {
                vector[i] = Float.parseFloat(parts[i].trim());
            }

            return vector;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
