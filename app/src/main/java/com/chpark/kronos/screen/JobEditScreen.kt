package com.chpark.kronos.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chpark.kronos.ui.viewmodel.JobViewModel
import com.chpark.kronos.util.showToast
import com.chpark.kronos.viewmodel.CodeEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmEditScreen(
    editorVm: CodeEditorViewModel,
    viewModel: JobViewModel = hiltViewModel(),
    alarmId: Long,
    onBack: () -> Unit,
    navController: NavHostController
) {
    val context = LocalContext.current

    // -------------------------------
    // Local states
    // -------------------------------
    var label by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var useCron by remember { mutableStateOf(true) }
    var cronExpression by remember { mutableStateOf("") }
    var useSu by remember { mutableStateOf(false) }
    var screenshot by remember { mutableStateOf(false) }
    // -------------------------------
    // DB load
    // -------------------------------
    val alarm by if (alarmId != 0L) {
        viewModel.getAlarmFlow(alarmId).collectAsState(initial = null)
    } else remember { mutableStateOf(null) }

    LaunchedEffect(alarm) {
        alarm?.let {
            label = it.name
            description = it.description
            useCron = it.useCron
            cronExpression = it.cronExpression
            useSu = it.useSu
            screenshot = it.enableScreenshot

            // CodeEditor의 초기 text 설정
            editorVm.reset(it.command)
        }
    }

    // CodeEditorScreen에서 돌아오면 editorVm.text 에 최신 코드가 존재
    val command = editorVm.text
    BackHandler {
        editorVm.clear()
        onBack()
    }
    // -------------------------------
    // UI
    // -------------------------------
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (alarmId == 0L) "알람 추가" else "알람 수정") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    // validation
                    if (label.isBlank()) {
                        showToast(context, "알람 이름은 필수입니다.")
                        return@Button
                    }
                    if (command.isBlank()) {
                        showToast(context, "스크립트가 비어 있습니다.")
                        return@Button
                    }
                    if (useCron && cronExpression.isBlank()) {
                        showToast(context, "Cron 표현식을 입력하세요.")
                        return@Button
                    }

                    viewModel.saveAlarm(
                        id = alarmId.takeIf { it != 0L },
                        name = label,
                        description = description,
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
                }
            ) {
                Text("저장")
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
            // 설명
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
            // SU 실행 옵션
            // -------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("root(su) 권한으로 실행")
                Switch(checked = useSu, onCheckedChange = { useSu = it })
            }

            Spacer(Modifier.height(20.dp))

            // -------------------------
            // Cron 활성화 여부
            // -------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("크론탭 활성화")
                Switch(checked = useCron, onCheckedChange = { useCron = it })
            }

            // Cron 활성화 시 Cron 입력칸 표시
            if (useCron) {
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = cronExpression,
                    onValueChange = { cronExpression = it },
                    label = { Text("Cron 표현식") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(20.dp))

            // -------------------------
            // Code Editor 실행
            // -------------------------
            Button(
                onClick = {
                    navController.navigate("code_editor")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("에디터 열기")
            }

            Spacer(Modifier.height(20.dp))

            // -------------------------
            // 현재 script 요약 표시
            // -------------------------
            Text("현재 스크립트:", color = Color.Gray)
            Text(
                text = command.take(200) + if (command.length > 200) "..." else "",
                color = Color.Black
            )

            Spacer(Modifier.height(20.dp))

            // -------------------------
            // Screenshot Option
            // -------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("스크린샷 저장")
                Switch(checked = screenshot, onCheckedChange = { screenshot = it })
            }

            Spacer(Modifier.height(160.dp))
        }
    }
}