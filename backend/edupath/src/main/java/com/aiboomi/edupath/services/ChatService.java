package com.aiboomi.edupath.services;

import com.aiboomi.edupath.dtos.ChatMessage;
import com.aiboomi.edupath.entities.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final FastRouterClient fastRouterClient;
    private final ProfileService profileService;

    public ChatService(FastRouterClient fastRouterClient, ProfileService profileService) {
        this.fastRouterClient = fastRouterClient;
        this.profileService = profileService;
    }

    public String processChat(Long studentId, List<ChatMessage> conversationHistory)
            throws Exception {
        // 1. Fetch Profile
        Profile profile = profileService.getLatestProfileForStudent(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for student " + studentId));

        String profileJson = profile.getProfileJson();

        // 2. Construct System Prompt
        String systemPrompt = "You are an expert career counselor for children. " +
                "You have access to the following student profile summary JSON: " + profileJson + "\n\n" +
                "Use this data to answer the user's questions responsibly and insightfully. " +
                "Focus on the student's strengths, interests, and potential paths identified in the profile. " +
                "If the data is insufficient, say so. " +
                "Keep your responses helpful, encouraging, and clear.";

        // 3. Prepare messages list
        List<Map<String, Object>> messages = new ArrayList<>();
        // Add system message first
        messages.add(Map.of("role", "system", "content", systemPrompt));

        // Add conversation history
        if (conversationHistory != null) {
            for (com.aiboomi.edupath.dtos.ChatMessage msg : conversationHistory) {
                messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
            }
        }

        // 4. Call FastRouter (Anthropic model via FastRouter)
        return fastRouterClient.chat(messages, "anthropic/claude-sonnet-4-20250514", 0.7, 1000);
    }
}
