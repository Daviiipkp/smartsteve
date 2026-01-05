package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.smartsteve.Instance.CommandE;
import com.daviipkp.smartsteve.services.CommandRegistry;
import org.springframework.stereotype.Component;

@CommandDescription(value = "Use to play a specific song or song style on Spotify.", possibleArguments = "Any song or audio that might be available on Spotify.")
public class PlayOnSpotifyCommand extends InstantCommand {

    public PlayOnSpotifyCommand() {
        setCommand(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
