package com.daviipkp.smartsteve.implementations.commands;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;

@CommandDescription(value = "Use to play a specific song or song style on Spotify. Anything that might be available on Spotify.",
        possibleArguments = "song_name: <String>",
        exampleUsage = "song_name: Stressed Out - Twenty One Pilots")
public class PlayOnSpotifyCommand extends InstantCommand {

    public PlayOnSpotifyCommand() {
        setCommand(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
