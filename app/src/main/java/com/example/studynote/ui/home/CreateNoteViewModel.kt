package com.example.studynote.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studynote.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CreateNoteUiState(
    val title: String = "",
    val content: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
)

class CreateNoteViewModel(
    private val repo: NoteRepository = NoteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateNoteUiState())
    val uiState: StateFlow<CreateNoteUiState> = _uiState

    fun onTitleChange(new: String) {
        _uiState.value = _uiState.value.copy(title = new, errorMessage = null)
    }

    fun onContentChange(new: String) {
        _uiState.value = _uiState.value.copy(content = new, errorMessage = null)
    }

    fun create(userId: Long) {
        val s = _uiState.value
        if (s.title.isBlank() || s.content.isBlank()) {
            _uiState.value = s.copy(errorMessage = "标题和内容不能为空")
            return
        }
        _uiState.value = s.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                repo.createNote(userId, s.title, s.content)
                _uiState.value = s.copy(isLoading = false, success = true)
            } catch (e: Exception) {
                _uiState.value = s.copy(isLoading = false, errorMessage = e.message ?: "创建失败")
            }
        }
    }

    /** 成功或取消后重置状态，以便下次重用 */
    fun reset() {
        _uiState.value = CreateNoteUiState()
    }
}
