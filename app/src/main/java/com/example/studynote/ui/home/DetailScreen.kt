package com.example.studynote.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studynote.data.repository.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    noteId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(noteId) {
        viewModel.load(noteId)
    }

    if (uiState.isLoading || uiState.note == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val note = uiState.note!!
    // 获取当前用户名
    val currentUsername by produceState<String?>(initialValue = null) {
        runCatching { AuthRepository().me().username }
            .onSuccess { value = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("笔记详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // 只有作者本人显示编辑/删除
                    if (note.author == currentUsername) {
                        IconButton(onClick = { onEdit(note.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "编辑")
                        }
                        IconButton(onClick = { onDelete(note.id) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 笔记标题、作者、内容
            Text(note.title, style = MaterialTheme.typography.headlineSmall)
            Text("作者：${note.author}", style = MaterialTheme.typography.bodyMedium)
            Divider()
            Text(note.snippet, style = MaterialTheme.typography.bodyMedium)

            Divider()

            // 评论列表
            Text("评论（${uiState.reviews.size}）", style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.reviews) { review ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) { idx ->
                                    Icon(
                                        imageVector = if (idx < review.rating) Icons.Default.Star
                                        else Icons.Outlined.StarOutline,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("用户 ${review.username}", style = MaterialTheme.typography.bodySmall)
                            }
                            review.comment?.let {
                                Spacer(Modifier.height(8.dp))
                                Text(it, style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(
                                review.createdAt,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }

            Divider()

            // 发布评论区
            Text("发表评论", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("评分：")
                repeat(5) { idx ->
                    IconButton(onClick = { viewModel.onRatingChange(idx + 1) }) {
                        Icon(
                            imageVector = if (idx < uiState.postRating) Icons.Default.Star
                            else Icons.Outlined.StarOutline,
                            contentDescription = "${idx + 1} 星"
                        )
                    }
                }
            }
            OutlinedTextField(
                value = uiState.postComment,
                onValueChange = viewModel::onCommentChange,
                placeholder = { Text("写下你的评价…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            uiState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = { viewModel.postReview(noteId) },
                modifier = Modifier.align(Alignment.End),
                enabled = !uiState.isPosting
            ) {
                if (uiState.isPosting) {
                    CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text("提交")
                }
            }
        }
    }
}
