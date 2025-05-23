package com.example.studynote.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studynote.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EditNoteUiState(
    val title: String = "",
    val content: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
)

class EditNoteViewModel(
    private val repo: NoteRepository = NoteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditNoteUiState())
    val uiState: StateFlow<EditNoteUiState> = _uiState

    /** 初始化：加载现有笔记内容 */
    fun load(noteId: Long) {
        _uiState.value = EditNoteUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val dto = repo.getNoteById(noteId)
                _uiState.value = EditNoteUiState(
                    title = dto.title,
                    content = dto.content,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = EditNoteUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "加载失败"
                )
            }
        }
    }

    fun onTitleChange(new: String) {
        _uiState.value = _uiState.value.copy(title = new, errorMessage = null)
    }

    fun onContentChange(new: String) {
        _uiState.value = _uiState.value.copy(content = new, errorMessage = null)
    }

    fun update(noteId: Long) {
        val s = _uiState.value
        if (s.title.isBlank() || s.content.isBlank()) {
            _uiState.value = s.copy(errorMessage = "标题和内容不能为空")
            return
        }
        _uiState.value = s.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                repo.updateNote(noteId, s.title, s.content)
                _uiState.value = s.copy(isLoading = false, success = true)
            } catch (e: Exception) {
                _uiState.value = s.copy(isLoading = false, errorMessage = e.message ?: "更新失败")
            }
        }
    }

    fun reset() {
        _uiState.value = EditNoteUiState()
    }
}
