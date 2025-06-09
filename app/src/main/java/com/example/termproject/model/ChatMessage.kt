package com.example.termproject.model

data class ChatMessage(
    val text: String,
    val isUser: Boolean,         // true: 사용자, false: 캐릭터
    val timestamp: Long = System.currentTimeMillis()
)
