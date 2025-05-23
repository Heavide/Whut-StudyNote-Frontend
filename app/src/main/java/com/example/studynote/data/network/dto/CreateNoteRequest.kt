// CreateNoteRequest.kt
package com.example.studynote.data.network.dto

data class CreateNoteRequest(
    val userId: Long,
    val title: String,
    val content: String
)
