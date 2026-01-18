package com.daviipkp.smartstevex.services;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.smartstevex.Configuration;
import com.daviipkp.smartstevex.Instance.Protocol;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtocolsService {

    //SmartSteve is a way for DEVs implement their own command in a Voice-based-interface
    //It executes commands implemented by the dev, whatever they are.
    //Smart Steve X

    public Map<Protocol, String> getProtocols(int top, String... query) {
        Map<Protocol, String> toReturn = new HashMap<>();
        if(query.length == 0 || (query.length == 1 && query[0].equals(""))) {
            return toReturn;
        }
        VectorStore vectorStore = SpringContext.getBean(VectorStore.class);
        String q = String.join(" ", query);
        SearchRequest request = SearchRequest.query(q)
                .withTopK(top)
                .withSimilarityThreshold(0.5);
        List<Document> r = List.of();
        try {
            r = vectorStore.similaritySearch(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(Configuration.MEMORY_DEBUG) {
            SteveCommandLib.systemPrint("Trying to get Similar protocol");
        }
        for (Document doc : r) {
            String c = doc.getContent();
            Map<String, Object> m = doc.getMetadata();
            try {
                String objType = (String)m.get("type");
                if(objType.equals("protocol")) {
                    toReturn.put(SteveJsoning.parse(c, Protocol.class), doc.getId());
                    if(Configuration.MEMORY_DEBUG) {
                        SteveCommandLib.systemPrint("Similar added: " + c);
                    }
                }
            }catch (Exception e) {
            }
        }
        return toReturn;

    }

}
