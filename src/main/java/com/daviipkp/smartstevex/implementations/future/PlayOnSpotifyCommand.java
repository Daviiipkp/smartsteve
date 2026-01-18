package com.daviipkp.smartstevex.implementations.future;

import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;

@CommandDescription(value = "Use to play a specific song or song style on Spotify. Anything that might be available on Spotify.",
        exampleUsage = "query: Stressed Out - Twenty One Pilots")
public class PlayOnSpotifyCommand extends InstantCommand {

    @Describe
    private String query;

    public PlayOnSpotifyCommand() {

    }
}
