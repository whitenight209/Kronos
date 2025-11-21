package com.chpark.kronos.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.chpark.kronos.util.getRawScriptList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptSelectorSheet(
    selected: String?,
    onSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val scripts = remember { getRawScriptList(context) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = selected ?: "",
            onValueChange = {},
            label = { Text("스크립트 선택") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showSheet = true }
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showSheet = false }
        ) {
            Text(
                "스크립트 선택",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            scripts.forEach { name ->
                ListItem(
                    headlineContent = { Text(name) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelected(name)
                            showSheet = false
                        }
                        .padding(horizontal = 8.dp)
                )
                Divider()
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}