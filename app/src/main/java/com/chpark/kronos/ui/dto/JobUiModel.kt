package com.chpark.kronos.ui.dto

import com.chpark.kronos.data.entity.JobEntity

data class JobUiModel(
    val entity: JobEntity,
    val isScheduled: Boolean
)