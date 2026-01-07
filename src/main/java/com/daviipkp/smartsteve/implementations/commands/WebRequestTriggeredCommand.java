package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.TriggeredCommand;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class WebRequestTriggeredCommand extends TriggeredCommand {

    private String request;
    private String provider;
    private String key;
    private HttpResponse<String> answer;

    @Override
    public void start() {
        super.start();
        new Thread(() -> {
            long time = System.currentTimeMillis();
            SteveCommandLib.systemPrint("Prompt sent: " + request);
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest r = HttpRequest.newBuilder()
                    .uri(URI.create(provider))
                    .header("Authorization", "Bearer " + key)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(request, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = null;
            try {
                response = client.send(r, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            SteveCommandLib.systemPrint("Request time: " + (System.currentTimeMillis() - time));

            if (response.statusCode() != 200) {
                System.err.println("error: " + response.statusCode());
                System.err.println("error bodyy: " + response.body());
            }
        }).start();

    }

    @Override
    public boolean checkTrigger(){
        return answer != null;
    }

    @Override
    public void handleError(Exception e) {

    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public void execute(long delta) {

    }
}
