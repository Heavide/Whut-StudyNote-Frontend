package com.example.studynote

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studynote.data.repository.NoteRepository
import com.example.studynote.ui.auth.LoginScreen
import com.example.studynote.ui.auth.RegisterScreen
import com.example.studynote.ui.home.*
import com.example.studynote.ui.profile.ProfileScreen
import com.example.studynote.ui.profile.ProfileViewModel
import com.example.studynote.ui.theme.StudyNoteTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyNoteTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val noteRepo = remember { NoteRepository() }

    // 登录／注册状态
    var isLoggedIn by remember {
        mutableStateOf(
            context
                .getSharedPreferences("auth", Context.MODE_PRIVATE)
                .getString("token", null) != null
        )
    }
    var isRegistering by remember { mutableStateOf(false) }
    var isShowingProfile by remember { mutableStateOf(false) }

    // 创建／编辑／详情状态
    var selectedNoteId by remember { mutableStateOf<Long?>(null) }
    var isCreatingNote by remember { mutableStateOf(false) }
    var isEditingNote by remember { mutableStateOf(false) }

    // HomeViewModel 和 ProfileViewModel
    val homeVm: HomeViewModel = viewModel()
    val homeState by homeVm.uiState.collectAsState()
    val profileVm: ProfileViewModel = viewModel()
    val profileState by profileVm.uiState.collectAsState()

    when {
        // 个人中心页面
        isLoggedIn && isShowingProfile -> {
            ProfileScreen(
                onLogout = {
                    isLoggedIn = false
                    isShowingProfile = false
                },
                onBackToHome = {
                    // 返回主页的逻辑
                    isShowingProfile = false
                }
            )
        }

        // 未登录 & 注册模式
        !isLoggedIn && isRegistering -> {
            RegisterScreen(
                onRegisterSuccess = { isRegistering = false },
                onNavigateToLogin = { isRegistering = false }
            )
        }

        // 未登录 & 登录模式
        !isLoggedIn && !isRegistering -> {
            LoginScreen(
                onLoginSuccess = { isLoggedIn = true },
                onNavigateToRegister = { isRegistering = true }
            )
        }

        // 已登录 & 创建新笔记模式
        isLoggedIn && isCreatingNote -> {
            CreateNoteScreen(
                onCreateSuccess = {
                    homeVm.loadNotes(reset = true)
                    isCreatingNote = false
                },
                onCancel = { isCreatingNote = false }
            )
        }

        // 已登录 & 编辑笔记模式
        isLoggedIn && isEditingNote && selectedNoteId != null -> {
            EditNoteScreen(
                noteId = selectedNoteId!!,
                onUpdateSuccess = {
                    homeVm.loadNotes(reset = true)
                    isEditingNote = false
                },
                onCancel = { isEditingNote = false }
            )
        }

        // 已登录 & 查看详情模式
        isLoggedIn && selectedNoteId != null -> {
            DetailScreen(
                noteId = selectedNoteId!!,
                onBack = { selectedNoteId = null },
                onEdit = { id ->
                    isEditingNote = true
                },
                onDelete = { id ->
                    scope.launch {
                        noteRepo.deleteNote(id)
                        homeVm.loadNotes(reset = true)
                        selectedNoteId = null
                    }
                }
            )
        }

        // 已登录 & 列表模式
        isLoggedIn -> {
            HomeScreen(
                notes = homeState.notes,
                keyword = homeState.keyword,
                onKeywordChange = { homeVm.onKeywordChange(it) },
                onNoteClick = { id -> selectedNoteId = id },
                onCreateNote = { isCreatingNote = true },
                onLogout = {
                    isShowingProfile = true
                },
                loadMore = { reset -> homeVm.loadNotes(reset) },
                isLastPage = homeState.isLastPage,
                isRefreshing = homeState.isLoading
            )
        }
    }
}
