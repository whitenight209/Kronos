package com.chpark.kronos.ui.components


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import com.chpark.kronos.data.entity.AlarmEntity
import com.chpark.kronos.util.formatTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmCard(
    alarm: AlarmEntity,
    onEdit: (AlarmEntity) -> Unit,
    onRunNow: (AlarmEntity) -> Unit,
    onDelete: (AlarmEntity) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ---------- 상단 Row: 이름 + 메뉴 ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = alarm.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "메뉴"
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("즉시 실행") },
                            leadingIcon = {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                            },
                            onClick = {
                                expanded = false
                                onRunNow(alarm)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("수정하기") },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            },
                            onClick = {
                                expanded = false
                                onEdit(alarm)
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("삭제") },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            },
                            onClick = {
                                expanded = false
                                onDelete(alarm)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ---------- 내용 ----------
            Text("Cron: ${alarm.cronExpression}", style = MaterialTheme.typography.bodyMedium)
            Text("Command: ${alarm.command}", style = MaterialTheme.typography.bodySmall)
            Text("Screenshot: ${alarm.enableScreenshot}", style = MaterialTheme.typography.bodySmall)

            val displayTime = remember(alarm.nextTriggerTime) {
                formatTime(alarm.nextTriggerTime)
            }
            Text("Next Trigger: $displayTime", style = MaterialTheme.typography.bodySmall)
        }
    }
}