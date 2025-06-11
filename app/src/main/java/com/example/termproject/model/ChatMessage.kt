package com.example.termproject.model

data class ChatMessage(
    var text: String = "",
    var isUser: Boolean = false,
    var timestamp: Long = 0L
) {
    // ✅ 명시적 기본 생성자 (Firebase 역직렬화를 위해 필요)
    constructor() : this("", false, 0L)
}