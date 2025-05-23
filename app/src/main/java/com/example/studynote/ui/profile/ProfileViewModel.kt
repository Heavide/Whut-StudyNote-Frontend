// ProfileViewModel.kt
package com.example.studynote.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studynote.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * UI 状态数据类
 */
data class ProfileUiState(
    val username: String = "",
    val email: String = "",
    val createdAt: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    /** 从后端 /auth/me 拉取当前用户信息 */
    fun loadProfile() {
        _uiState.value = ProfileUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val user = repo.me()
                _uiState.value = ProfileUiState(
                    username = user.username,
                    email = user.email,
                    createdAt = user.createdAt,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "加载失败"
                )
            }
        }
    }
}
