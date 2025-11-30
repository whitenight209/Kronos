package com.chpark.kronos.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chpark.kronos.ui.components.AlarmCard
import com.chpark.kronos.data.entity.JobEntity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController

import com.chpark.kronos.ui.viewmodel.JobViewModel

@Composable
fun AlarmListScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val jobViewModel: JobViewModel = hiltViewModel()
    val jobs by jobViewModel.uiJobs.collectAsState()
    Column(
    ) {
        if (false) {
            EmptyAlarmView()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
//                    .border(
//                        width = 1.dp,
//                        color = MaterialTheme.colorScheme.outline,
//                        shape = RectangleShape
//                    ),
                        ,
                contentPadding = PaddingValues(
                    top = 8.dp,     // ✔ TopBar 밑에 딱 적당한 여백
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(jobs, key = { it.entity.id }) { job ->
                    AlarmCard(
                        alarm = job.entity,
                        onEdit = { openEditScreen(navController, it) },
                        onRunNow = {
                            runAlarmNow(
                                context = context,
                                alarm = it,
                                viewModel = jobViewModel
                            )
                        },
                        onDelete = { jobViewModel.deleteAlarm(it) }
                    )
                }
            }
        }
    }
}



private fun openEditScreen(navController: NavHostController, alarm: JobEntity) {
    navController.navigate("alarm_edit/${alarm.id}")
}

// ✔ runNow 수정
private fun runAlarmNow(
    context: Context,
    alarm: JobEntity,
    viewModel: JobViewModel
) {
    viewModel.runNow(context, alarm)
    Toast.makeText(context, "즉시 실행되었습니다.", Toast.LENGTH_SHORT).show()
}

@Composable
private fun EmptyAlarmView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "등록된 알람이 없습니다.",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "오른쪽 하단의 + 버튼을 눌러 알람을 추가하세요.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
