package com.chpark.kronos.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.SyncStateContract.Helpers.update
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chpark.kronos.ui.viewmodel.AlarmViewModel
import com.chpark.kronos.util.showToast

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmEditScreen(
    alarmId: Long,
    onBack: () -> Unit,
    navController: NavHostController,
    viewModel: AlarmViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // -------------------------------
    // State
    // -------------------------------
    var label by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }     // ★ NEW

    var useCron by remember { mutableStateOf(true) }
    var cronExpression by remember { mutableStateOf("") }

    var command by remember { mutableStateOf("") }
    var useSu by remember { mutableStateOf(false) }
    var screenshot by remember { mutableStateOf(false) }
    var script by remember { mutableStateOf("echo hello\n") }
    val result = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("result_script")
        ?.observeAsState()

    result?.value?.let { newValue ->
        script = newValue
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.remove<String>("result_script")
    }
    // -----------------------------------
    // DB 로드
    // -----------------------------------
    val alarm by if (alarmId != 0L) {
        viewModel.getAlarmFlow(alarmId).collectAsState(initial = null)
    } else {
        mutableStateOf(null)
    }

    LaunchedEffect(alarm) {
        alarm?.let {
            label = it.name
            description = it.description                     // ★ NEW
            useCron = it.useCron
            cronExpression = it.cronExpression
            command = it.command
            useSu = it.useSu
            screenshot = it.enableScreenshot
        }
    }


    // -----------------------------------
    // UI
    // -----------------------------------
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (alarmId == 0L) "알람 추가" else "알람 수정") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier
            ) {
                Button(
                    onClick = {
                        if (label.isBlank() || command.isBlank()) {
                            showToast(context, "필수 항목을 입력해주세요.")
                            return@Button
                        }

                        viewModel.saveAlarm(
                            id = alarmId.takeIf { it != 0L },
                            name = label,
                            description = description,          // ★ NEW
                            useCron = useCron,
                            cronExpression = cronExpression,
                            command = command,
                            useSu = useSu,
                            enableScreenshot = screenshot,
                            onDone = { saved ->
                                viewModel.scheduleIfCronExpressionNotNull(context, saved)
                                showToast(context, "저장되었습니다.")
                                onBack()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) { Text("저장") }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxSize()
        ) {

            // -------------------------
            // 알람 이름
            // -------------------------
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("알람 이름") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // -------------------------
            // 설명 (Description) ★ NEW
            // -------------------------
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("설명") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(Modifier.height(20.dp))

            // -------------------------
            // SU 토글
            // -------------------------
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("root(su) 권한으로 실행")
                Switch(checked = useSu, onCheckedChange = { useSu = it })
            }

            Spacer(Modifier.height(20.dp))

            // -------------------------
            // Cron 활성/비활성
            // -------------------------
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("크론탭 활성화")
                Switch(checked = useCron, onCheckedChange = { useCron = it })
            }

            Spacer(Modifier.height(12.dp))

            if (useCron) {
                OutlinedTextField(
                    value = cronExpression,
                    onValueChange = { cronExpression = it },
                    label = { Text("Cron 표현식") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(20.dp))
            }
            // -------------------------
            // Bash Code Editor
            // -------------------------
            Button(
                onClick = {
                    navController.navigate("code_editor")
                }
            ) {
                Text("에디터 열기")
            }
            Spacer(Modifier.height(20.dp))

            // -------------------------
            // Screenshot
            // -------------------------
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("스크린샷 저장")
                Switch(checked = screenshot, onCheckedChange = { screenshot = it })
            }

            Spacer(Modifier.height(120.dp))
        }
    }
}

