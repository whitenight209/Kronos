package com.chpark.kronos.util

import android.content.Context
import android.util.Log
import com.chpark.kronos.data.entity.AlarmEntity
import com.chpark.kronos.data.entity.ExecutionHistoryEntity
import com.chpark.kronos.data.repository.ExecutionHistoryRepository
import java.io.DataOutputStream
import java.io.File

class ScriptExecutor(
    private val context: Context,
    private val alarm: AlarmEntity,
    private val isScheduled: Boolean,
    private val historyRepo: ExecutionHistoryRepository
) {

    private var historyId = 0L
    private val outputBuffer = StringBuilder()
    private var screenshotIndex = 1

    companion object {
        private const val TAG = "ScriptExecutor"
    }

    // ─────────────────────────────────────────────
    // 1) History 생성
    // ─────────────────────────────────────────────
    private fun createHistory() {
        val his = ExecutionHistoryEntity().apply {
            alarmId = alarm.id.toInt()
            label = alarm.name
            executedAt = System.currentTimeMillis()
            success = false
        }

        historyId = historyRepo.insert(his)
        Log.i(TAG, "History created id=$historyId")
    }

    // ─────────────────────────────────────────────
    // 2) History 업데이트
    // ─────────────────────────────────────────────
    private fun updateHistory(success: Boolean, exitCode: Int = 0) {
        val his = historyRepo.findById(historyId) ?: return

        his.success = success
        his.exitCode = exitCode
        his.output = outputBuffer.toString()

        historyRepo.update(his)
        Log.i(TAG, "History updated id=$historyId success=$success")
    }

    // ─────────────────────────────────────────────
    // 스크립트 읽기
    // ─────────────────────────────────────────────


    private fun readCommand(): List<String> {
        val result = mutableListOf<String>()
        val split = alarm.command.split("\n")
        split.forEach {
            val line = it.trim()
            if (!isScheduled && line.contains("keyevent")) {
                val skipList = mutableListOf<Int?>(224, 82, 3, 223)
                val keyCode: Int = parseKeyEvent(line)
                if (skipList.contains(keyCode)) return@forEach
            }
            if (line.isEmpty()) return@forEach
            if (line.startsWith("#!") ||
                (line.startsWith("#") && !line.startsWith("#screenshot"))
            ) return@forEach
            result.add(line)
        }

        return result
    }

    private fun parseKeyEvent(keyEvent: String): Int {
        if (keyEvent.trim { it <= ' ' }.isEmpty()) {
            throw RuntimeException("key event can't be null!")
        }
        return keyEvent.split("keyevent".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[1].trim { it <= ' ' }.toInt()
    }

    // ─────────────────────────────────────────────
    // Shell 명령 실행
    // ─────────────────────────────────────────────
    private fun execShell(cmd: String) {
        try {
            val pb = ProcessBuilder("su", "-c", cmd)
            pb.redirectErrorStream(true)
            val process = pb.start()

            process.inputStream.bufferedReader().use { br ->
                br.lineSequence().forEach { line ->
                    outputBuffer.appendLine(line)
                }
            }

            val exit = process.waitFor()
            Log.i(TAG, "exec exit=$exit cmd=$cmd")

        } catch (e: Exception) {
            Log.e(TAG, "execShell error", e)
        }
    }

    // ─────────────────────────────────────────────
    // 스크린샷 저장
    // ─────────────────────────────────────────────
    private fun saveScreenshot() {
        try {
            val baseDir = File(context.getExternalFilesDir("history"), historyId.toString())
            if (!baseDir.exists()) baseDir.mkdirs()

            val outFile = File(baseDir, "${screenshotIndex++}.png")
            execShell("screencap -p ${outFile.absolutePath}")

            val his = historyRepo.findById(historyId)
            if (his != null) {
                his.screenshotPath = baseDir.absolutePath
                historyRepo.update(his)
            }

        } catch (e: Exception) {
            Log.e(TAG, "saveScreenshot error", e)
        }
    }

    // ─────────────────────────────────────────────
    // 전체 실행
    // ─────────────────────────────────────────────
    fun execute(): Boolean {
        var success = false
        val scriptLines = readCommand()
        createHistory()
        try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)

            for (line in scriptLines) {
                Log.i(TAG, "exec line: $line")

                outputBuffer.appendLine("[CMD] $line")

                when {
                    line.startsWith("#screenshot") && alarm.enableScreenshot -> {
                        saveScreenshot()
                    }

                    line.startsWith("sleep ") -> {
                        val sec = line.removePrefix("sleep").trim().toLongOrNull() ?: 0L
                        Thread.sleep(sec * 1000L)
                    }

                    else -> {
                        os.writeBytes("$line\n")
                        os.flush()
                    }
                }
            }

            os.writeBytes("exit\n")
            os.flush()
            os.close()

            val exitCode = process.waitFor()
            success = (exitCode == 0)

        } catch (e: Exception) {
            Log.e(TAG, "execute error", e)
            success = false
        }

        updateHistory(success)
        return success
    }
}