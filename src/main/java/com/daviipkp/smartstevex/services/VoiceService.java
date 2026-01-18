package com.daviipkp.smartstevex.services;

import org.springframework.stereotype.Service;
import javax.sound.sampled.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class VoiceService {

    private static final String CURRENT_DIR = System.getProperty("user.dir");

    private static final String PIPER_FOLDER = CURRENT_DIR + File.separator + "piper";

    private static final String PIPER_EXE = PIPER_FOLDER + File.separator + "piper.exe";

    private static final String MODEL_PATH = PIPER_FOLDER + File.separator + "en_GB-alan-medium.onnx";
    private static final String TEMP_AUDIO_FILE = PIPER_FOLDER + File.separator + "debug_audio.wav";

    private static Thread speakThread;

    private static float volume = 1;

    public static void speak(String text) {
        shutUp();
        speakThread = new Thread(() -> {
            try {
                generateWavFile(text);
                EarService s = SpringContext.getBean(EarService.class);
                s.stopListening();

                playWavFile();

                s.resumeListening();
                shutUp();
            } catch (Exception e) {
                System.out.println("Error trying to speak (Play LLM response): " + e.getMessage());
            }
        });
        speakThread.start();
    }

    public static void speak(String text, Runnable onFinish) {
        new Thread(() -> {
            try {
                generateWavFile(text);
                playWavFile();

            } catch (Exception e) {
                System.out.println("Error trying to speak (Play LLM response): " + e.getMessage());
            } finally {
                if (onFinish != null) {
                    onFinish.run();
                }
            }
        }).start();
    }

    public static void shutUp() {
        if(speakThread != null) {
            speakThread.interrupt();
        }
    }

    public static Thread getCurrentThread() {
        return speakThread;
    }

    private static void generateWavFile(String text) throws IOException, InterruptedException {
        String safeText = text.replace("\n", " ").replace("\"", "");

        ProcessBuilder pb = new ProcessBuilder(
                PIPER_EXE,
                "--model", MODEL_PATH,
                "--output_file", TEMP_AUDIO_FILE
        );

        pb.directory(new File(PIPER_FOLDER));
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);

        Process process = pb.start();

        try (OutputStream os = process.getOutputStream()) {
            os.write(safeText.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("Error while trying to generate Wav File.");
        } else {
            File f = new File(TEMP_AUDIO_FILE);
        }
    }

    public static void setVolume(float newVolume) {
        if (newVolume < 0f) volume = 0f;
        else volume = Math.min(newVolume, 1f);
    }

    private static void playWavFile() {
        try {
            File audioFile = new File(TEMP_AUDIO_FILE);
            if (!audioFile.exists() || audioFile.length() < 100) return;

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                float dB = (float) (Math.log(volume != 0 ? volume : 0.0001) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            }


            clip.start();
            Thread.sleep(clip.getMicrosecondLength() / 1000);

            clip.close();
            audioStream.close();
        } catch (Exception e) {
            System.out.println("Error trying to play Wav File: " + e.getMessage());
        }
    }
}