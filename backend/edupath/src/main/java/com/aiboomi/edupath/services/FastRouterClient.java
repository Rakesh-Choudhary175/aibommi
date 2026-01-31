package com.aiboomi.edupath.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Component
public class FastRouterClient {
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    @Value("${fastrouter.api.key}")
    private String apiKey;

    public FastRouterClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    public String chat(List<Map<String, Object>> messages, String model, double temperature, int maxTokens)
            throws Exception {
        Map<String, Object> payload = Map.of(
                "model", model,
                "messages", messages,
                "temperature", temperature,
                "max_tokens", maxTokens,
                "stream", false // Disable stream for simple Rest API usage
        );

        String body = objectMapper.writeValueAsString(payload);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://go.fastrouter.ai/api/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 400) {
            throw new RuntimeException("FastRouter API error: " + resp.statusCode() + " - " + resp.body());
        }

        Map<?, ?> parsed = objectMapper.readValue(resp.body(), Map.class);
        var choices = (java.util.List<?>) parsed.get("choices");
        if (choices == null || choices.isEmpty())
            return "";
        Map<?, ?> first = (Map<?, ?>) choices.get(0);
        Object message = first.get("message");
        if (message instanceof Map) {
            Object content = ((Map<?, ?>) message).get("content");
            return content == null ? "" : content.toString().trim();
        }
        return "";
    }
}
