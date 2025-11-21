package com.chpark.kronos.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import com.chpark.kronos.ui.viewmodel.ExecutionHistoryViewModel
import com.chpark.kronos.util.showToast

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: ExecutionHistoryViewModel = hiltViewModel()

    var openDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {

        Text(
            "설정",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { openDialog = true }) {
            Text("실행 히스토리 전체 삭제")
        }

        if (openDialog) {
            AlertDialog(
                onDismissRequest = { openDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.clearAll()
                        showToast(context, "히스토리가 삭제되었습니다.")
                        openDialog = false
                    }) {
                        Text("삭제")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { openDialog = false }) {
                        Text("취소")
                    }
                },
                text = {
                    Text("전체 실행 기록을 삭제하시겠습니까? 되돌릴 수 없습니다.")
                }
            )
        }
    }
}