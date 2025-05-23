package com.example.studynote.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.collect
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.calculateEndPadding

/**
 * 列表项的数据模型
 */
data class NoteItem(
    val id: Long,
    val title: String,
    val author: String,
    val snippet: String
)

/**
 * 笔记列表界面，包含：
 * - 搜索框
 * - 下拉刷新（Pull-to-Refresh）
 * - 滚动自动分页加载
 * - 新建笔记 / 登出 按钮
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    notes: List<NoteItem>,
    keyword: String,
    onKeywordChange: (String) -> Unit,
    onNoteClick: (Long) -> Unit,
    onCreateNote: () -> Unit,
    onLogout: () -> Unit,
    loadMore: (reset: Boolean) -> Unit,
    isLastPage: Boolean,
    isRefreshing: Boolean
) {

    // 列表状态，用于监测滚动位置
    val listState = rememberLazyListState()

    // 当滚动接近底部时自动触发下一页加载
    LaunchedEffect(listState) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalCount = listState.layoutInfo.totalItemsCount
            lastVisible to totalCount
        }.collect { (lastVisible, totalCount) ->
            if (lastVisible != null && lastVisible >= totalCount - 3 && !isLastPage) {
                loadMore(false)
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("学习笔记列表") },
                    actions = {
                        TextButton(onClick = onCreateNote) { Text("新建") }
                        TextButton(onClick = onLogout)    { Text("个人中心") }
                    }
                )
                // 搜索框直接与上方的顶部应用栏紧凑地结合
                OutlinedTextField(
                    value = keyword,
                    onValueChange = onKeywordChange,
                    placeholder = { Text("搜索标题或内容") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp) // 这里可以适当调整padding来减少间距
                )
            }
        }
    ) { innerPadding ->
        // 去掉外部的padding，使得内容更加贴近顶部
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { loadMore(true) },
            modifier = Modifier.padding(innerPadding)
        ) {

            LazyColumn(
                state = listState,
                // 控制list的内容边距，减少和顶部搜索框的空白
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notes) { note ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNoteClick(note.id) }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(note.title, maxLines = 1)
                            Text("作者：${note.author}", maxLines = 1, modifier = Modifier.padding(top = 4.dp))
                            Text(note.snippet, maxLines = 2, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
                // 非最后一页时在底部展示加载指示
                if (!isLastPage) {
                    item {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}


/**
 * 扩展：将内边距与统一外部间距合并
 */
private fun PaddingValues.addPadding(dp: Dp): PaddingValues {
    return PaddingValues(
        start  = this.calculateStartPadding(LayoutDirection.Ltr) + dp,
        top    = this.calculateTopPadding() + dp,
        end    = this.calculateEndPadding(LayoutDirection.Ltr) + dp,
        bottom = this.calculateBottomPadding() + dp
    )
}
