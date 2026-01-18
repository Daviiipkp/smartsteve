package com.daviipkp.smartstevex.services;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.smartstevex.Configuration;
import lombok.Getter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;

@Service
public class EarService {

    private final KeyboardService kb;
    private final DualBrainService brain;
    private TargetDataLine microphone;
    private volatile boolean isPaused = false;
    @Getter
    private boolean voiceTyping;

    public EarService(KeyboardService kb, DualBrainService brain) {
        this.kb = kb;
        this.brain = brain;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        new Thread(this::startListening).start();
    }

    public void stopListening() {
        SteveCommandLib.systemPrint(">>> Paused listening...");
        this.isPaused = true;
        if (microphone != null && microphone.isOpen()) {
            microphone.stop();
            microphone.flush();
        }
    }

    public void resumeListening() {
        SteveCommandLib.systemPrint(">>> Listening again...");
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
                System.err.println("Error with Audio System.");
                return;
            }
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            byte[] buffer = new byte[4096];
            int bytesRead;

            while (true) {

                if(isPaused) {
                    Thread.sleep(200);
                   continue;
                }

                bytesRead = microphone.read(buffer, 0, buffer.length);

                if(bytesRead > 0) {
                    if(isVoiceTyping() && Configuration.VOICE_TYPING_FEATURE) {
                        if(recognizer.acceptWaveForm(buffer, bytesRead)) {
                            String jsonResult = recognizer.getResult();
                            String text = extractTextFromVosk(jsonResult);
                            if(text.contains(Configuration.VOICE_TYPING_STOP_STRING)) {
                                DualBrainService.setVoiceTyping(false);
                                continue;
                            }
                            kb.typeText(text);

                        }
                    }else{
                        if(VoiceService.getCurrentThread() != null && VoiceService.getCurrentThread().isAlive()) {
                            if(recognizer.acceptWaveForm(buffer, bytesRead)) {
                                String jsonResult = recognizer.getResult();
                                String text = extractTextFromVosk(jsonResult);
                                if(text.contains("steve")) {
                                    if(text.contains("shut up")) {
                                        VoiceService.shutUp();
                                    }
                                }
                            }
                        }
                        if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                            String jsonResult = recognizer.getResult();
                            String text = extractTextFromVosk(jsonResult);
                            if (text.contains(Configuration.VOICE_START_WORD) && Configuration.USE_VOICE_START_WORD) {
                                if (text.contains(Configuration.VOICE_END_WORD) && Configuration.USE_VOICE_END_WORD) {
                                    text = text.replace(Configuration.VOICE_END_WORD, "");
                                    if(Configuration.SHOW_VOICE_TEXT_DEBUG) {
                                        SteveCommandLib.systemPrint("User said: " + text);
                                    }
                                    brain.processUserPrompt(text);
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