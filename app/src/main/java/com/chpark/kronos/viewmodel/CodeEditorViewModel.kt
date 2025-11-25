package com.chpark.kronos.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class CodeEditorViewModel @Inject constructor() : ViewModel() {

var text by mutableStateOf("")
    private set

fun updateText(t: String) { text = t }

fun reset(t: String) { text = t }   // AlarmEditScreen에서 초기 값 주입

fun clear() { text = "" }
}