package com.daviipkp.smartstevex.implementations.future;

import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;

@CommandDescription(value = "Command used to search anything in the web. User inputs, recent happenings. Anything that might be in the internet.")
public class SearchWebCommand extends WebRequestTriggeredCommand {


    @Describe
    private String query;

    @Override
    public void start() {
        super.start();
        //Web request logic
    }

    @Override
    public boolean checkTrigger() {
        //Web request returned logic (if returned, AI Callback)
        return false;
    }

    @Override
    public void handleError(Exception e) {

    }
}
