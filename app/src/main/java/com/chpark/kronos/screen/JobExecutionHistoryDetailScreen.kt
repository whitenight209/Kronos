package com.chpark.kronos.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.chpark.kronos.data.entity.ExecutionHistoryEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExecutionHistoryDetailScreen(
    entity: ExecutionHistoryEntity,
    onBack: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("실행 상세") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // --- Title Row ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = entity.label ?: "(라벨 없음)",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                AssistChip(
                    onClick = {},
                    label = { Text(if (entity.success) "성공" else "실패") },
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

            Spacer(Modifier.height(12.dp))

            // --- Basic Info ---
            Text(
                text = "실행 시간: ${sdf.format(Date(entity.executedAt))}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Exit Code: ${entity.exitCode}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            // --- Commands ---
            if (!entity.commands.isNullOrBlank()) {
                Text(
                    text = "Commands",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))

                Surface(
                    tonalElevation = 2.dp,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = entity.commands!!,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(20.dp))
            }

            // --- Output ---
            if (!entity.output.isNullOrBlank()) {
                Text(
                    text = "Output",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))

                Surface(
                    tonalElevation = 2.dp,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = entity.output!!,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(20.dp))
            }

            // --- Screenshot ---
            if (!entity.screenshotPath.isNullOrBlank()) {
                Text(
                    text = "스크린샷",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                AsyncImage(
                    model = entity.screenshotPath!!,
                    contentDescription = "스크린샷",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
