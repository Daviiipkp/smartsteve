package com.daviipkp.smartsteve;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.smartsteve.prompt.Prompt;
import com.daviipkp.smartsteve.services.LLMService;
import com.daviipkp.smartsteve.services.SpringContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartsteveApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SmartsteveApplication.class)
                .headless(false)
                .run(args);

        for (String arg : args) {
            if (arg.equals("--FirstBoot")) {
                SpringContext.getBean(LLMService.class).finalCallModel(Prompt.getStartupPrompt());
            }
        }
        LLMService.warmUp();
        java.security.Security.setProperty("networkaddress.cache.ttl", "-1");
        if(Constants.CLEAR_MEMO_ON_STARTUP) {
            try {
                JdbcTemplate jdbcTemplate = SpringContext.getBean(JdbcTemplate.class);
                jdbcTemplate.execute("TRUNCATE TABLE vector_store");
                jdbcTemplate.execute("TRUNCATE TABLE chat_message");
                SteveCommandLib.systemPrint("Memory cleared");
            } catch (Exception e) {
                SteveCommandLib.systemPrint("error" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
