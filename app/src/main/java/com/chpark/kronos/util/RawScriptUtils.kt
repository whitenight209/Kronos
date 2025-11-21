package com.chpark.kronos.util

import android.content.Context
import com.chpark.kronos.R

fun getRawScriptList(context: Context): List<String> {
    // R.raw 클래스 안의 모든 필드를 reflection 으로 가져옴.
    val rawClass = R.raw::class.java

    return rawClass.fields.map { it.name }
}