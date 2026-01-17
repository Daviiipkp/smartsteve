package com.daviipkp.smartsteve.services;

import org.springframework.stereotype.Service;
import javax.sound.sampled.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class VoiceService {

    private static final String PIPER_FOLDER = "D:\\Coding\\Projects\\smartsteve\\piper";
    private static final String PIPER_EXE = PIPER_FOLDER + "\\piper.exe";
    private static final String MODEL_PATH = PIPER_FOLDER + "\\en_GB-alan-medium.onnx";

    private static final String TEMP_AUDIO_FILE = "D:\\Coding\\Projects\\smartsteve\\piper\\debug_audio.wav";

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
                e.printStackTrace();
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
                e.printStackTrace();
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
            System.err.println("ERRRRRRRRRRR");
        } else {
            File f = new File(TEMP_AUDIO_FILE);
            System.out.println("Size: " + f.length() + " bytes.");
        }
    }

    public static void setVolume(float newVolume) {
        if (newVolume < 0f) volume = 0f;
        else if (newVolume > 1f) volume = 1f;
        else volume = newVolume;
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
            e.printStackTrace();
        }
    }
}