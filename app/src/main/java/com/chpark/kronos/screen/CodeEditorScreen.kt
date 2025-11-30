package com.chpark.kronos.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.chpark.kronos.viewmodel.CodeEditorViewModel
import io.github.rosemoe.sora.widget.CodeEditor
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeEditorScreen(
    navController: NavHostController,
    editorVm: CodeEditorViewModel
) {
    val context = LocalContext.current
    val editorRef = remember { mutableStateOf<CodeEditor?>(null) }

    // 파일 Import Launcher
    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val content = context.contentResolver.openInputStream(uri)
                ?.bufferedReader()
                ?.readText()

            if (!content.isNullOrBlank()) {
                editorVm.updateText(content)
            }
        }
    }
    BackHandler {
        val finalText = editorRef.value?.text.toString()
        editorVm.updateText(finalText)
        navController.popBackStack()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("스크립트 편집") },
                navigationIcon = {
                    IconButton(onClick = {
                        val finalText = editorRef.value?.text.toString()
                        editorVm.updateText(finalText)
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            // 파일 불러오기
                            filePicker.launch(
                                arrayOf("*/*")
                            )
                        }
                    ) {
                        Text("불러오기")
                    }
                }
            )
        }
    ) { padding ->

        AndroidView(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            factory = { ctx ->
                CodeEditor(ctx).apply {
                    // 초기 Text 설정
                    setText(editorVm.text)
                    editorRef.value = this
                }
            },

            update = { editor ->
                // ViewModel.text 와 UI desync 방지
                val current = editor.text.toString()
                if (current != editorVm.text) {
                    editor.setText(editorVm.text)
                }
            }
        )
    }
}
