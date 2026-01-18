package com.daviipkp.smartstevex.implementations.future;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveCommandLib.instance.InstantCommand;
import com.daviipkp.SteveJsoning.annotations.CommandDescription;
import com.daviipkp.SteveJsoning.annotations.Describe;

import java.awt.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@CommandDescription(value = "Use to play a specific video or song on YouTube.",
        exampleUsage = "query: Stressed Out - Twenty One Pilots")
public class PlayOnYoutubeCommand extends InstantCommand {

    @Describe
    private String query;

    public PlayOnYoutubeCommand() {
        setCommand(() -> {

            if (query == null || query.trim().isEmpty()) {
                return;
            }

            try {
                String encodedQuery = URLEncoder.encode(query + " youtube", StandardCharsets.UTF_8);
                String youtubeUrl = "https://www.youtube.com/results?search_query=" + encodedQuery + "&autoplay=1";
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI(youtubeUrl));
                } else {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + youtubeUrl);
                }

            } catch (Exception e) {
                SteveCommandLib.systemPrint("err: " + e.getMessage());
            }
        });
    }

}
