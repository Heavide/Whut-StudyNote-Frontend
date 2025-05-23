package com.example.studynote.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studynote.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val message: String = "") : AuthUiState()
    data class Error(val error: String) : AuthUiState()
}

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val loginState: StateFlow<AuthUiState> = _loginState

    private val _registerState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val registerState: StateFlow<AuthUiState> = _registerState

    /** 执行登录 */
    fun login(usernameOrEmail: String, password: String) {
        _loginState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                repo.login(usernameOrEmail, password)
                _loginState.value = AuthUiState.Success()
            } catch (e: Exception) {
                _loginState.value = AuthUiState.Error(e.message ?: "登录失败")
            }
        }
    }

    /** 执行注册 */
    fun register(username: String, email: String, password: String) {
        _registerState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                repo.register(username, email, password)
                _registerState.value = AuthUiState.Success("注册成功，请登录")
            } catch (e: Exception) {
                _registerState.value = AuthUiState.Error(e.message ?: "注册失败")
            }
        }
    }

    /** 重置登录状态 */
    fun resetLoginState() {
        _loginState.value = AuthUiState.Idle
    }

    /** 重置注册状态 */
    fun resetRegisterState() {
        _registerState.value = AuthUiState.Idle
    }
}
