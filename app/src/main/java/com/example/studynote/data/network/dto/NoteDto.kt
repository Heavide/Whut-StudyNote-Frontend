// NoteDto.kt
package com.example.studynote.data.network.dto

data class NoteDto(
    val id: Long,
    val userId: Long,
    val authorName: String,
    val title: String,
    val snippet: String,    // 后端返回的简短内容字段
    val content: String,    // 如果需要完整内容
    val createdAt: String,
    val updatedAt: String
)
