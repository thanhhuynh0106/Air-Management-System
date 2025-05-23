package com.ams.airManagement.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.net.http.*;
import java.util.*;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @PostMapping("/all")
    public ResponseEntity<?> chatWithBot(@RequestBody Map<String, String> requestBody) {
        try {
            String userMessage = requestBody.get("message");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(new JSONObject()
                            .put("model", "gpt-3.5-turbo")
                            .put("messages", List.of(
                                    new JSONObject().put("role", "system").put("content", "Bạn là trợ lý đặt vé máy bay. Trả lời ngắn gọn và chính xác."),
                                    new JSONObject().put("role", "user").put("content", userMessage)
                            ))
                            .toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            String reply = json.getJSONArray("choices").getJSONObject(0)
                    .getJSONObject("message").getString("content");

            System.out.println("Response from OpenAI: " + response.body());

            return ResponseEntity.ok(Map.of("reply", reply));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
