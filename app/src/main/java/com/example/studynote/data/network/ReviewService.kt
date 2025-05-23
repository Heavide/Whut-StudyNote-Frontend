// ReviewService.kt
package com.example.studynote.data.network

import com.example.studynote.data.network.dto.ReviewDto
import com.example.studynote.data.network.dto.ReviewRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewService {
    @GET("notes/{id}/reviews")
    suspend fun getReviews(@Path("id") noteId: Long): List<ReviewDto>

    @POST("notes/{id}/reviews")
    suspend fun postReview(
        @Path("id") noteId: Long,
        @Body req: ReviewRequest
    ): ReviewDto
}
