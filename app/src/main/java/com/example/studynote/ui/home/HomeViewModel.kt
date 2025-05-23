package com.example.studynote.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studynote.data.repository.NoteRepository
import com.example.studynote.data.network.dto.NoteDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val notes: List<NoteItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val keyword: String = "",
    val page: Int = 0,
    val isLastPage: Boolean = false
)

class HomeViewModel(
    private val repo: NoteRepository = NoteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadNotes(reset = true)
    }

    fun onKeywordChange(newKeyword: String) {
        _uiState.value = _uiState.value.copy(keyword = newKeyword)
        loadNotes(reset = true)
    }

    fun loadNotes(reset: Boolean = false) {
        val current = _uiState.value
        if (current.isLoading || current.isLastPage && !reset) return

        val nextPage = if (reset) 0 else current.page + 1
        _uiState.value = current.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val dtos: List<NoteDto> =
                    repo.getNotes(page = nextPage, size = 20, keyword = current.keyword.takeIf { it.isNotBlank() })
                // 转换成 UI 模型
                val items = dtos.map { dto ->
                    NoteItem(
                        id = dto.id,
                        title = dto.title,
                        author = dto.authorName /* 如果后端返回 authorName 可直接替换 */,
                        snippet = dto.content.take(80)
                    )
                }
                val allItems = if (reset) items else current.notes + items
                val last = dtos.size < 20

                _uiState.value = HomeUiState(
                    notes = allItems,
                    isLoading = false,
                    errorMessage = null,
                    keyword = current.keyword,
                    page = nextPage,
                    isLastPage = last
                )
            } catch (e: Exception) {
                _uiState.value = current.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "加载失败"
                )
            }
        }
    }
}
