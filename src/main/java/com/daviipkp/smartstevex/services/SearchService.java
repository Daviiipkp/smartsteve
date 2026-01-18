package com.daviipkp.smartstevex.services;

import com.daviipkp.smartstevex.Configuration;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

@Service
public class SearchService {

    private final RestClient restClient;

    public SearchService() {
        this.restClient = RestClient.create();
    }

    public String searchAndSummarize(String query) {
        try {
            BraveResponse response = restClient.get()
                    .uri(URI.create(Configuration.SEARCH_PROVIDER))
                    .header("Authorization", "Bearer " + Configuration.SEARCH_API_KEY)
                    .retrieve()
                    .body(BraveResponse.class);

            if (response != null && response.web() != null && response.web().results() != null) {
                return formatResultsForLLM(response.web().results());
            }

            return "No results!";

        } catch (Exception e) {
            return "Error trying to access the internet: " + e.getMessage();
        }
    }

    private String formatResultsForLLM(List<SearchResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("Internet context (up to date information):\n");

        for (SearchResult r : results) {
            sb.append("- [").append(r.title()).append("]\n");
            sb.append("  Content: ").append(r.description()).append("\n");
            sb.append("  Source: ").append(r.url()).append("\n");
            sb.append("\n");
        }
        return sb.toString();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record BraveResponse(WebSearch web) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record WebSearch(List<SearchResult> results) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SearchResult(String title, String url, String description) {}


}
