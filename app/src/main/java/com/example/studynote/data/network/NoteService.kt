package com.example.studynote.data.network

import com.example.studynote.data.network.dto.CreateNoteRequest
import com.example.studynote.data.network.dto.NoteDto
import retrofit2.Response
import retrofit2.http.*

interface NoteService {
    @GET("notes")
    suspend fun getNotes(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("keyword") keyword: String? = null
    ): List<NoteDto>

    @GET("notes/{id}")
    suspend fun getNoteById(@Path("id") id: Long): NoteDto

    @POST("notes")
    suspend fun createNote(@Body req: CreateNoteRequest): NoteDto

    @PUT("notes/{id}")
    suspend fun updateNote(
        @Path("id") id: Long,
        @Body req: CreateNoteRequest
    ): NoteDto

    @DELETE("notes/{id}")
    suspend fun deleteNote(@Path("id") id: Long): Response<Unit>  // 修改为返回 Response<Unit>
}
