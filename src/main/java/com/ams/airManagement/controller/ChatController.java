package com.ams.airManagement.controller;

import com.ams.airManagement.service.interfac.ChatServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatServiceInterface chatService;

    @PostMapping("/all")
    public ResponseEntity<?> chatWithBot(@RequestBody Map<String, String> requestBody) {
        try {
            String userMessage = requestBody.get("message");
            if (userMessage == null || userMessage.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty"));
            }

            String reply = chatService.getChatResponse(userMessage);

            return ResponseEntity.ok(Map.of("reply", reply));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }
}