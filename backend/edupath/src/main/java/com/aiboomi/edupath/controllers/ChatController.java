package com.aiboomi.edupath.controllers;

import com.aiboomi.edupath.dtos.ChatRequest;
import com.aiboomi.edupath.dtos.ChatResponse;
import com.aiboomi.edupath.services.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            String reply = chatService.processChat(request.getStudentId(), request.getMessages());
            return ResponseEntity.ok(new ChatResponse(reply));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ChatResponse("Error: " + e.getMessage()));
        }
    }
}
