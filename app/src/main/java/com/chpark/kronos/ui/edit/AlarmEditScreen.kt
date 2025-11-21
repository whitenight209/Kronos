package com.chpark.kronos.ui.edit

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.chpark.kronos.ui.components.ScriptSelectorDropdown
import com.chpark.kronos.ui.viewmodel.AlarmViewModel
import com.chpark.kronos.util.showToast

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmEditScreen(
    alarmId: Long,
    onBack: () -> Unit,
    viewModel: AlarmViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // -------------------------------
    // 1) UI State
    // -------------------------------
    var label by remember { mutableStateOf("") }
    var cron by remember { mutableStateOf("") }
    var selectedScript by remember { mutableStateOf<String?>(null) }
    var screenshot by remember { mutableStateOf(false) }

    // -------------------------------
    // 2) DB에서 Alarm 불러오기 (Flow)
    // -------------------------------
    val alarm by if (alarmId != 0L) {
        viewModel.getAlarmFlow(alarmId).collectAsState(initial = null)
    } else {
        mutableStateOf(null)
    }

    // -------------------------------
    // 3) UI 초기화
    // -------------------------------
    LaunchedEffect(alarmId, alarm) {
        alarm?.let {
            label = it.name
            cron = it.cronExpression
            selectedScript = it.command
            screenshot = it.enableScreenshot
        }
    }

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
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Button(
                    onClick = {

                        if (label.isBlank() || cron.isBlank() || selectedScript.isNullOrBlank()) {
                            showToast(context, "모든 필드를 입력해주세요.")
                            return@Button
                        }

                        viewModel.saveAlarm(
                            id = alarmId.takeIf { it != 0L },
                            name = label,
                            cronExpression = cron,
                            command = selectedScript!!,
                            enableScreenshot = screenshot,
                            onDone = { savedAlarm ->
                                viewModel.schedule(context, savedAlarm)
                                showToast(context, "저장되었습니다.")
                                onBack()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("저장")
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {

            // 알람 이름
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("알람 이름") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Cron
            OutlinedTextField(
                value = cron,
                onValueChange = { cron = it },
                label = { Text("Cron 표현식") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // 스크립트 선택 Dialog
            ScriptSelectorDropdown(
                selected = selectedScript,
                onSelected = { selectedScript = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // 스크린샷 옵션
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("스크린샷 저장")
                Switch(
                    checked = screenshot,
                    onCheckedChange = { screenshot = it }
                )
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}