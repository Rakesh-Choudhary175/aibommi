package com.aiboomi.edupath.dtos;

import java.util.List;

public class ChatRequest {
    private Long studentId;
    private List<ChatMessage> messages;

    public ChatRequest() {
    }

    public ChatRequest(Long studentId, List<ChatMessage> messages) {
        this.studentId = studentId;
        this.messages = messages;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
}
