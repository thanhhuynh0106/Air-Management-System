package com.ams.airManagement.service.implement;

import com.ams.airManagement.repository.FlightsRepository;
import com.ams.airManagement.service.interfac.ChatServiceInterface;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ChatServiceImpl implements ChatServiceInterface {

    @Autowired
    private FlightsRepository flightsRepository;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Override
    public String getChatResponse(String message) {
        if (message.toLowerCase().contains("chuyến bay") || message.contains("HCM") || message.contains("Hà Nội")) {
            long count = flightsRepository.count();
            return "Hiện có " + count + " chuyến bay đang được khai thác.";
        }

        return callOpenAi(message);
    }

    private String callOpenAi(String prompt) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String body = """
            {
              "model": "gpt-3.5-turbo",
              "messages": [{"role": "user", "content": "%s"}]
            }
            """.formatted(prompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            return json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

        } catch (Exception e) {
            return "Xin lỗi, hiện tại tôi không thể trả lời. Vui lòng thử lại sau.";
        }
    }
}
