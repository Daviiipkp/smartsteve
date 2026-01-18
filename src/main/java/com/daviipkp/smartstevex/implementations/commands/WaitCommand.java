package com.daviipkp.smartstevex.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.QueuedCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;

@CommandDescription(value = "Forces the Command Queue to wait before executing another command. No point adding this command if there is no command after it. Always talk BEFORE this command if you want it to be instant.")
public class WaitCommand extends QueuedCommand {

    @Describe
    private long time;

    private long counter = 0;

    @Override
    public void handleError(Exception e) {}

    @Override
    public void execute(long delta) {
        this.counter += delta;
        if(this.counter >= (time * 1000)) {
            finish();
        }
        super.execute(delta);
    }

}
