package com.chpark.kronos.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.platform.LocalContext
import com.chpark.kronos.util.getRawScriptList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptSelectorDropdown(
    selected: String?,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scripts = remember { getRawScriptList(context) }

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {

        OutlinedTextField(
            value = selected ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("스크립트 선택") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            scripts.forEach { name ->
                DropdownModernItem(
                    label = name,
                    onClick = {
                        onSelected(name)
                        expanded = false
                    }
                )
            }
        }
    }
}