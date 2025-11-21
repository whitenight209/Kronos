package com.chpark.kronos.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chpark.kronos.data.entity.ExecutionHistoryEntity
import com.chpark.kronos.ui.viewmodel.ExecutionHistoryViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.Color
import android.graphics.BitmapFactory
import androidx.compose.foundation.clickable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

enum class HistoryFilter { ALL, SUCCESS, FAILURE }

@Composable
fun ExecutionHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: ExecutionHistoryViewModel = hiltViewModel(),
    onItemClick: (ExecutionHistoryEntity) -> Unit
) {
    val histories by viewModel.histories.collectAsState()
    var filter by remember { mutableStateOf(HistoryFilter.ALL) }

    val filtered = when (filter) {
        HistoryFilter.ALL -> histories
        HistoryFilter.SUCCESS -> histories.filter { it.success }
        HistoryFilter.FAILURE -> histories.filter { !it.success }
    }

    Column(modifier = modifier.fillMaxSize()) {

        FilterChipsRow(
            filter = filter,
            onFilterChange = { filter = it }
        )

        HorizontalDivider()

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "실행 이력이 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { item ->
                    HistoryItemCard(
                        entity = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChipsRow(
    filter: HistoryFilter,
    onFilterChange: (HistoryFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        FilterChip(
            selected = filter == HistoryFilter.ALL,
            onClick = { onFilterChange(HistoryFilter.ALL) },
            label = { Text("전체") }
        )
        FilterChip(
            selected = filter == HistoryFilter.SUCCESS,
            onClick = { onFilterChange(HistoryFilter.SUCCESS) },
            label = { Text("성공") }
        )
        FilterChip(
            selected = filter == HistoryFilter.FAILURE,
            onClick = { onFilterChange(HistoryFilter.FAILURE) },
            label = { Text("실패") }
        )
    }
}

@Composable
fun HistoryItemCard(
    entity: ExecutionHistoryEntity,
    onClick: () -> Unit
) {
    val sdf = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable{onClick()},
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // 1) Title Row: Label + 성공/실패
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = entity.label ?: "(라벨 없음)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                AssistChip(
                    onClick = {},
                    label = {
                        Text(if (entity.success) "성공" else "실패")
                    },
                    leadingIcon = {
                        Icon(
                            if (entity.success) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (entity.success)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer
                    )
                )
            }

            Spacer(Modifier.height(4.dp))

            // 2) 날짜 + exitCode
            Text(
                text = "${sdf.format(Date(entity.executedAt))}  |  exitCode=${entity.exitCode}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 3) commands
            if (!entity.commands.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = entity.commands!!,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 4) output
            if (!entity.output.isNullOrBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = entity.output!!,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 5) Screenshot thumbnail
            if (!entity.screenshotPath.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))

                ScreenshotPreview(path = entity.screenshotPath!!)
            }
        }
    }
}

@Composable
fun ScreenshotPreview(path: String) {
    val bitmap = remember(path) {
        kotlin.runCatching { BitmapFactory.decodeFile(path) }.getOrNull()
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "스크린샷",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(top = 4.dp),
            contentScale = ContentScale.Crop
        )
    } else {
        Text(
            text = "스크린샷을 불러올 수 없습니다",
            color = Color.Red,
            style = MaterialTheme.typography.bodySmall
        )
    }
}