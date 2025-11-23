package com.chpark.kronos.screen

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.chpark.kronos.ui.viewmodel.AlarmViewModel
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeEditorScreen(
//    initialCode: String,
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    val context = LocalContext.current
    val alarmViewModel: AlarmViewModel = hiltViewModel()
    val editorState by mutableStateOf(
        CodeEditorState()
    )
    // 코드 초기화
    LaunchedEffect(Unit) {

    }

    // 파일 Import
    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val text = context.contentResolver.openInputStream(uri)
                ?.bufferedReader()
                ?.readText()
            if (text != null) {
//                editorState.setText(text)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("스크립트 편집") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
//                        val code = editorState.text.toString()
//                        onSave(code)
                    }) {
                        Icon(Icons.Default.Save, "Save")
                    }

                    TextButton(
                        onClick = {
                            filePicker.launch(arrayOf("text/plain", "application/x-sh"))
                        }
                    ) {
                        Text("불러오기")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .background(Color(0xFF1E1E1E))
                .fillMaxSize()
        ) {
            CodeEditorComposable(
                modifier = Modifier.fillMaxSize(),
                state = CodeEditorState()
            )
        }
    }
}


@Composable
fun CodeEditorComposable(
    modifier: Modifier = Modifier,
    state: CodeEditorState
) {
    val context = LocalContext.current
    val editor = remember {
        setCodeEditorFactory(
            context = context,
            state = state
        )
    }
    AndroidView(
        factory = { editor },
        modifier = modifier,
        onRelease = {
            it.release()
        }
    )
}

data class CodeEditorState(
    var editor: CodeEditor? = null,
    val initialContent: Content = Content()
) {
    var content by mutableStateOf(initialContent)
}

private fun setCodeEditorFactory(
    context: Context,
    state: CodeEditorState
): CodeEditor {
    val editor = CodeEditor(context)
    editor.apply {
        setText(state.content)
    }
    state.editor = editor
    return editor
}