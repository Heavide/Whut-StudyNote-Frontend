package com.example.studynote.data.repository

import com.example.studynote.App
import com.example.studynote.data.network.ReviewService
import com.example.studynote.data.network.dto.ReviewDto
import com.example.studynote.data.network.dto.ReviewRequest

class ReviewRepository {

    private val service = App.retrofit.create(ReviewService::class.java)

    /** 获取指定笔记的所有评论 */
    suspend fun getReviews(noteId: Long): List<ReviewDto> {
        return service.getReviews(noteId)
    }

    /** 发布新的评论 */
    suspend fun postReview(noteId: Long, rating: Int, comment: String?): ReviewDto {
        val req = ReviewRequest(rating, comment)
        return service.postReview(noteId, req)
    }
}
