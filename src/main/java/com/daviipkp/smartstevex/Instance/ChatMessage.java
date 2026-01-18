package com.daviipkp.smartstevex.Instance;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class ChatMessage {
    //Transform all this with embedding models!
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String userPrompt;

    @Column(columnDefinition = "TEXT")
    private String steveResponse;

    public ChatMessage(String userPrompt, String steveResponse) {
        this.userPrompt = userPrompt;
        this.steveResponse = steveResponse;
        this.timestamp = LocalDateTime.now();
    }
}
