package com.chpark.kronos.util

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import java.time.ZoneId
import java.time.ZonedDateTime

object CronUtilsHelper {
    private val parser = CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX))

    fun getNextExecution(cronExpr: String?): Long {
        val cron = parser.parse(cronExpr)
        val executionTime = ExecutionTime.forCron(cron)

        val now = ZonedDateTime.now(ZoneId.systemDefault())

        val next = executionTime.nextExecution(now).orElse(null)

        return next?.toInstant()?.toEpochMilli()
            ?: now.plusMinutes(1).toInstant().toEpochMilli()
    }
}