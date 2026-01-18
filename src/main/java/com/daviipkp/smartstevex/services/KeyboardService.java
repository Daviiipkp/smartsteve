package com.daviipkp.smartstevex.services;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

@Service
public class KeyboardService {

    private final Robot robot;

    public KeyboardService() {
        try {
            this.robot = new Robot();

            this.robot.setAutoDelay(40);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void typeText(String text) {
        StringSelection s = new StringSelection(text);
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(s, s);
        try {
            robot.keyPress(KeyEvent.VK_CONTROL);
            clickButton(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clickButton(int key) {
        robot.keyPress(key);
        robot.keyRelease(key);
    }

}
