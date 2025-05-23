// ReviewDto.kt
package com.example.studynote.data.network.dto

data class ReviewDto(
    val id: Long,
    val noteId: Long,
    val userId: Long,
    val rating: Int,
    var username: String,
    val comment: String?,
    val createdAt: String
)
