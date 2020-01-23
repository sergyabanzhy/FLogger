package com.library.logger.writer

import com.library.logger.getDigitsFromName
import com.library.logger.getMbSize
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors.newSingleThreadExecutor

class LogWriter(private val filesDir: File) {

    private val dateFormatter: SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private val stringBuilder = StringBuilder()
    var maxSize: Long = 0

    private var currentFile = File(filesDir, "log1.txt")

    private val executor: Executor by lazy {
        newSingleThreadExecutor()
    }

    private val queue: LogItemsQueue<EntryLog> by lazy {
        LogItemsQueue<EntryLog>()
    }

    fun queueEntryLog(log: EntryLog) {
        queue.put(log)
    }

    private fun formatString(log: EntryLog): StringBuilder {

        val str = stringBuilder.append(currentTime(), " [${log.tag}] ", log.message)

        log.t?.run {
            str.append(message)
        }

        insertSeparatorInLogLineIfNeeded(str)

        return str
    }

    private fun getActualFile(file: File, str: StringBuilder): File {

        return if (isFreeSpaceInFileForContent(file, str)) {
            file
        } else {
            defineNextFile(file)
        }
    }

    private fun defineNextFile(prevFile: File): File {
        var fileIndex = prevFile.getDigitsFromName()
        fileIndex++
        currentFile = File(filesDir, "log$fileIndex.txt")
        return currentFile
    }

    private fun isFreeSpaceInFileForContent(file: File, content: StringBuilder): Boolean {
        return file.getMbSize() + (content.length * 2) <= maxSize
    }

    private fun writeToFile(str: StringBuilder) {

        try {
            val buf = BufferedWriter(FileWriter(getActualFile(currentFile, str), true))
            buf.append(str)
            buf.newLine()
            buf.close()
            stringBuilder.clear()
        } catch (e: IOException) {
            Timber.e("File write failed: $e")
        }
    }

    private fun currentTime(): String {
        return dateFormatter.format(Calendar.getInstance().time)
    }

    fun init() {

        queue.queueChangedListener = {
            executor.execute {
                try {
                    while (queue.isNotEmpty()) {

                        queue.poll()?.run {
                            writeToFile(formatString(this))
                        }

                    }

                } catch (e: InterruptedException) {
                    Timber.e("File write failed: $e")
                }
            }
        }
    }

    private fun insertSeparatorInLogLineIfNeeded(log: StringBuilder) {

        val length = log.length
        if (length > 180) {

            var lines = length / 180

            while (lines > 0) {
                log.insert(180 * lines, System.lineSeparator())
                lines--
            }
        }
    }

    fun getLogFiles(block: (Array<File>) -> (Unit)) {
        val files = filesDir.listFiles { pathname -> pathname.name.startsWith("log") }

        files?.run {
            block(files)
        }
    }
}