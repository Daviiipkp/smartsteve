package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.smartsteve.Instance.Command;
import com.daviipkp.smartsteve.services.CommandRegistry;
import org.springframework.stereotype.Component;

@Component
public class PlayOnSpotifyCommand extends Command {
    @Override
    public void execute() {

    }

    @Override
    public void callback() {

    }

    @Override
    public void executeSupCallback() {

    }

    @Override
    public String getID() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return "Use to play a specific song or song style on Spotify. Example usage: " + CommandRegistry.getExampleUsage(getID(), "Stressed Out - Twenty One Pilots");
    }
}
