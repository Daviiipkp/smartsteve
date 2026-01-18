package com.daviipkp.smartstevex.controller;

import com.daviipkp.smartstevex.services.DualBrainService;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/steve")
public class SteveController {
    private final DualBrainService dbs;

    public SteveController(DualBrainService dbs) {
        this.dbs = dbs;
    }

    @GetMapping("/talked")
    public String userTalked(@RequestParam String command) throws ExecutionException, InterruptedException {
        return dbs.processUserPrompt(command);
    }

    @PostMapping("/chat")
    public String  userChat(@RequestParam String command) throws ExecutionException, InterruptedException {
        return dbs.processUserPrompt(command);
    }

}
