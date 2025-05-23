package com.example.studynote.data.repository

import com.example.studynote.App
import com.example.studynote.data.network.NoteService
import com.example.studynote.data.network.dto.CreateNoteRequest
import com.example.studynote.data.network.dto.NoteDto

class NoteRepository {

    private val service = App.retrofit.create(NoteService::class.java)

    /** 分页查询笔记 */
    suspend fun getNotes(page: Int, size: Int, keyword: String?): List<NoteDto> {
        return service.getNotes(page, size, keyword)
    }

    /** 查询单条笔记详情 */
    suspend fun getNoteById(id: Long): NoteDto {
        return service.getNoteById(id)
    }

    /** 创建新笔记 */
    suspend fun createNote(userId: Long, title: String, content: String): NoteDto {
        val req = CreateNoteRequest(userId, title, content)
        return service.createNote(req)
    }

    /** 更新笔记 */
    suspend fun updateNote(id: Long, title: String, content: String): NoteDto {
        val req = CreateNoteRequest(0, title, content)
        return service.updateNote(id, req)
    }

    /** 删除笔记 */
    suspend fun deleteNote(id: Long) {
        service.deleteNote(id)
    }
}
