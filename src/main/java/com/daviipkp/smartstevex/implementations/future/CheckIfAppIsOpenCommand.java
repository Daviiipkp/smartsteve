package com.daviipkp.smartstevex.implementations.future;

import com.daviipkp.SteveCommandLib.instance.TriggeredCommand;

public class CheckIfAppIsOpenCommand extends TriggeredCommand {
    @Override
    public boolean checkTrigger() {
        return false;
    }

    @Override
    public void handleError(Exception e) {

    }
    //Checks if a software passed as argument for the command is in computer's process list
    //In the arguments, AI is instructed to give another command as well to happen if process is open and yet another command if process is not open
    //all logic is executed in Java
}
