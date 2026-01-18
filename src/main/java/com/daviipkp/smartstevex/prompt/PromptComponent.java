package com.daviipkp.smartstevex.prompt;

import lombok.*;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromptComponent {

    private String header = "";
    private String content = "";
    private String footer = "";

    public String getHeader() {
        return header==null?"":header;
    }

    public String getContent() {
        return content==null?"":content;
    }

    public String getFooter() {
        return footer==null?"":footer;
    }

}
