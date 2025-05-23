package com.example.studynote.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studynote.data.repository.NoteRepository
import com.example.studynote.data.repository.ReviewRepository
import com.example.studynote.data.network.dto.ReviewDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** 界面用的评论条目模型 */
data class ReviewItem(
    val id: Long,
    val userId: Long,
    val username: String,
    val rating: Int,
    val comment: String?,
    val createdAt: String
)

/** Detail 页面的 UI 状态 */
data class DetailUiState(
    val isLoading: Boolean = false,
    val note: NoteItem? = null,               // 可选 note 对象
    val reviews: List<ReviewItem> = emptyList(),
    val postRating: Int = 0,
    val postComment: String = "",
    val isPosting: Boolean = false,
    val errorMessage: String? = null
)

class DetailViewModel(
    private val noteRepo: NoteRepository = NoteRepository(),
    private val reviewRepo: ReviewRepository = ReviewRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState

    /** 加载笔记详情和评论 */
    fun load(noteId: Long) {
        _uiState.value = DetailUiState(isLoading = true)
        viewModelScope.launch {
            try {
                // 1. 拉笔记详情
                val dto = noteRepo.getNoteById(noteId)
                val noteItem = NoteItem(
                    id = dto.id,
                    title = dto.title,
                    author = dto.authorName,
                    snippet = dto.content // 或者 content.take(80) 取摘要
                )
                // 2. 拉评论列表
                val reviews = reviewRepo.getReviews(noteId).map { r: ReviewDto ->
                    ReviewItem(
                        id = r.id,
                        userId = r.userId,
                        rating = r.rating,
                        comment = r.comment,
                        username = r.username,
                        createdAt = r.createdAt
                    )
                }
                // 3. 更新状态
                _uiState.value = DetailUiState(
                    isLoading = false,
                    note = noteItem,
                    reviews = reviews
                )
            } catch (e: Exception) {
                _uiState.value = DetailUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "加载失败"
                )
            }
        }
    }

    fun onRatingChange(newRating: Int) {
        _uiState.value = _uiState.value.copy(postRating = newRating, errorMessage = null)
    }

    fun onCommentChange(newComment: String) {
        _uiState.value = _uiState.value.copy(postComment = newComment, errorMessage = null)
    }

    fun postReview(noteId: Long) {
        val state = _uiState.value
        if (state.postRating !in 1..5) {
            _uiState.value = state.copy(errorMessage = "请选择 1-5 星")
            return
        }
        _uiState.value = state.copy(isPosting = true, errorMessage = null)
        viewModelScope.launch {
            try {
                reviewRepo.postReview(
                    noteId = noteId,
                    rating = state.postRating,
                    comment = state.postComment.takeIf { it.isNotBlank() }
                )
                // 发布后重新拉评论
                val updated = reviewRepo.getReviews(noteId).map { r ->
                    ReviewItem(r.id, r.userId, r.username, r.rating, r.comment, r.createdAt)
                }
                _uiState.value = state.copy(
                    isPosting = false,
                    postRating = 0,
                    postComment = "",
                    reviews = updated
                )
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isPosting = false,
                    errorMessage = e.message ?: "提交失败"
                )
            }
        }
    }
}
