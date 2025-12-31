package com.daviipkp.smartsteve.services;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.IOException;

@Service
public class EarService {

    private final DualBrainService brain;
    private TargetDataLine microphone;
    private boolean isRunning = true;
    private volatile boolean isPaused = false;

    public EarService(DualBrainService brain) {
        this.brain = brain;
        new Thread(this::startListening).start();
    }

    public void stopListening() {
        System.out.println(">>> Paused listening...");
        this.isPaused = true;
        if (microphone != null && microphone.isOpen()) {
            microphone.stop();
            microphone.flush();
        }
    }

    public void resumeListening() {
        System.out.println(">>> Listening again...");
        if (microphone != null && microphone.isOpen()) {
            microphone.start();
        }
        this.isPaused = false;
    }
    private void startListening() {
        try {
            Model model = new Model("model_en");
            Recognizer recognizer = new Recognizer(model, 16000f);

            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("!!!!!!!!!");
                return;
            }

            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            byte[] buffer = new byte[4096];
            int bytesRead;

            System.out.println(">>> Listening");

            while (isRunning) {
                if (isPaused) {
                    Thread.sleep(100);
                    continue;
                }

                bytesRead = microphone.read(buffer, 0, buffer.length);

                if (bytesRead > 0) {
                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        String jsonResult = recognizer.getResult();
                        String text = extractTextFromVosk(jsonResult);

                        if (!text.trim().isEmpty()) {
                            System.out.println("User said: " + text);

                            stopListening();

                            brain.processCommand(text);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractTextFromVosk(String json) {
        int start = json.indexOf(": \"") + 3;
        int end = json.lastIndexOf("\"");
        if (start > 3 && end > start) {
            return json.substring(start, end);
        }
        return "";
    }
}