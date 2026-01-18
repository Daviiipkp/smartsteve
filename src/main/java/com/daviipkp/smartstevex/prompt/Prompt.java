package com.daviipkp.smartstevex.prompt;

import com.daviipkp.SteveCommandLib.SteveCommandLib;
import com.daviipkp.SteveJsoning.SteveJsoning;
import com.daviipkp.smartstevex.Configuration;
import com.daviipkp.smartstevex.Instance.Protocol;
import com.daviipkp.smartstevex.Utils;
import com.daviipkp.smartstevex.services.DualBrainService;
import com.daviipkp.smartstevex.services.ProtocolsService;
import com.daviipkp.smartstevex.services.SpringContext;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Prompt {

    private static final DualBrainService dbs = SpringContext.getBean(DualBrainService.class);

    public static String getDefaultPrompt(String userPrompt) {
        return createPrompt(system_role, system_rules, output_format, getCommandList(userPrompt), getProtocolList(), getContext(), getMemoryConsultation(userPrompt), getUserPrompt(userPrompt));
    }

    public static String getCallBackPrompt(String instructions, String context) {
        return createPrompt(system_role, system_rules, output_format, getCommandList(instructions, context), getProtocolList(), getContext(context), getSystemInstructions(instructions));
    }

    public static String getStartupPrompt() {
        return createPrompt(system_role, system_rules, output_format, getCommandList(first_boot.getContent()), first_boot);
    }

    public static String createPrompt(PromptComponent... components) {
        StringBuilder sb = new StringBuilder();
        sb.append("Prompt Created at: ").append(LocalDateTime.now()).append("\n");
        for (PromptComponent component : components) {
            if(!component.getContent().isEmpty()) {
                sb.append("\n");
                sb.append("### ").append(component.getHeader().toUpperCase());
                sb.append("\n");
                sb.append(component.getContent());
                sb.append("\n");
                sb.append(component.getFooter());
            }else{
                if(Configuration.PROMPT_COMPONENTS_CONTENT_EMPTY_DEBUG) {
                    SteveCommandLib.systemPrint(component.getHeader());
                }
            }
        }
        return sb.toString();
    }

    public static String addComponents(String prompt, PromptComponent... components) {
        StringBuilder sb = new StringBuilder();
        sb.append(prompt);
        for (PromptComponent component : components) {
            sb.append("\n");
            sb.append("### ").append(component.getHeader().toUpperCase());
            sb.append(component.getContent());
            sb.append("\n");
            sb.append(component.getFooter());
        }
        return sb.toString();
    }



    private static PromptComponent getMemoryConsultation(String userPrompt) {
        return PromptComponent.builder().header("memory consultation").content(dbs.getMemoryConsult(userPrompt)).build();
    }

    private static PromptComponent getContext() {
        return PromptComponent.builder().header("prompt context")
                .content(dbs.getContext()).build();
    }

    private static PromptComponent getContext(String arg0) {
        return PromptComponent.builder().header("prompt context")
                .content(arg0).build();
    }

    private static PromptComponent getSystemInstructions(String instructions) {
        return PromptComponent.builder().header("system instructions")
                .content(instructions).build();
    }

    private static PromptComponent getCommandList(String... query) {
        return PromptComponent.builder().header("list of available commands")
                .content(Utils.getCommandNamesWithDesc()).build();
    }

    private static PromptComponent getProtocolList(String... query) {
        StringBuilder sb = new StringBuilder();
        for(Protocol p : SpringContext.getBean(ProtocolsService.class).getProtocols(Configuration.PROTOCOL_SEARCH_NUMBER, query).keySet()) {
            sb.append(SteveJsoning.stringify(p)).append("\n");
        }
        if(sb.toString().isEmpty()) {
            sb.append("no protocols available.");
        }
        return PromptComponent.builder().header("list of available protocols")
                .content(sb.toString()).build();
    }

    private static PromptComponent getUserPrompt(String userPrompt) {
        return PromptComponent.builder().header("user prompt")
                .content(userPrompt).build();
    }
    private static final PromptComponent system_role = PromptComponent.builder().header("system role")
                .content("""
                        You are Steve, an ultra-efficient assistant.
                        ALWAYS respect the Json mandatory system.
                        Return ONLY the JSON object. Do not include any conversational text, thought process, or markdown code blocks (like ```json).
                        """).build();

    private static final PromptComponent output_format = PromptComponent.builder().header("output format - mandatory")
            .content("""
                        You must ONLY return a raw JSON object. No markdown, no preambles. Follow THIS structure:
                        {
                          "status": "SUCCESS", "DOING", "IGNORE",
                          "action": {
                            "COMMAND_ID": {
                              "argument": "value"
                            }
                          },
                          "memory": "Concise log of what just happened (User intent + Your Action) to serve as context for the NEXT turn."
                        }
                        
                        EXAMPLE Structure:
                        {
                          "status": "SUCCESS",
                          "action": {
                            "SystemShutdownCommand": {
                              "time": "20"
                            },
                            "TalkCommand": {
                                "message": "Yes, sir. Shutting down in 20."
                            }
                          },
                          "memory": "User asked me to shutdown the system in 20 seconds. I sent the command to do it."
                        }
                        """).build();

    private static final PromptComponent system_rules = PromptComponent.builder().header("system rules")
            .content("""
                        1. Be extremely direct on your responses.
                        2. If it's asked for the user and not system instructions, for successful command executions, the preferred speech is simply "Yes, sir." (Implies: "Done").
                        3. Know the difference: for simple calls like "Hey, Steve!", the preferred speech is simply "Yes, sir?" (Implies: "What's your request?")
                        4. Strict Command Matching: NEVER invent commands. Check the AVAILABLE COMMANDS list.
                        """).build();

    private static final PromptComponent first_boot = PromptComponent.builder().header("first boot system instructions")
            .content(Configuration.FIRST_BOOT_INSTRUCTIONS).build();

}
