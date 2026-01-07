package com.daviipkp.smartsteve.services;

import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.smartsteve.Instance.ChatMessage;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class EarService {

    private final KeyboardService kb;
    private final DualBrainService brain;
    private final VoiceService voiceService;
    private TargetDataLine microphone;
    private boolean isRunning = true;
    private volatile boolean isPaused = false;
    @Getter
    private boolean voiceTyping;

    public EarService(KeyboardService kb, DualBrainService brain, VoiceService voiceService) {
        this.kb = kb;
        this.brain = brain;
        new Thread(this::startListening).start();
        this.voiceService = voiceService;
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



                if(bytesRead > 0) {
                    if(isVoiceTyping()) {
                        if(!recognizer.acceptWaveForm(buffer, bytesRead)) {

                        }
                        else {

                            String jsonResult = recognizer.getResult();
                            String text = extractTextFromVosk(jsonResult);
                            if(text.contains("stop")) {
                                DualBrainService.setVoiceTyping(false);
                                continue;
                            }
                            kb.typeText(text);

                        }
                    }else{
                        if(VoiceService.getCurrentThread() != null && VoiceService.getCurrentThread().isAlive()) {
                            return;
                        }
                        if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                            String jsonResult = recognizer.getResult();
                            String text = extractTextFromVosk(jsonResult);
                            if(text.contains("steve")) {
                                if(text.contains("shut up")) {
                                    VoiceService.shutUp();
                                    return;
                                }
                                System.out.println(SteveJsoning.stringify(new ChatMessage("a","b","c","d")));
                                if (!text.trim().isEmpty()) {
                                    System.out.println("User said: " + text);

                                    stopListening();

                                    brain.processCommand(text);
                                }

                            }


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
    public void startVoiceTyping() {
        voiceTyping = true;
    }public void stopVoiceTyping() {
        voiceTyping = false;
    }
}