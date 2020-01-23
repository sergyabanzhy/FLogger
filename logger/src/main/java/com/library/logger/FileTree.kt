package com.library.logger

import com.library.logger.writer.EntryLog
import com.library.logger.writer.LogWriter
import timber.log.Timber
import java.io.File
import java.lang.IllegalArgumentException

class FileTree(private val logWriter: LogWriter): Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        logWriter.queueEntryLog(EntryLog(tag, message, t))
    }

    /**
     * Returns an array of log files
     * @param block - this function will be invoked once files became ready
     */
    fun getLogFiles(block:(Array<File>) -> (Unit)) {
        logWriter.getLogFiles {
            block(it)
        }
    }

    companion object

    /**
     * Use Builder to initialize FileTree with parameters
     */
    class Builder {
        private lateinit var fileDir: File
        private var fileSize: Long = 1 * 1024 * 1024
        private lateinit var logWriter: LogWriter

        /**
         * Provide application's directory to store log files
         */
        fun filesDir(file: File) = apply { this.fileDir = file }

        /**
         * Provide one log file's size. By default it's 1 * 1024 * 1024 (1Mb)
         * Once file's size riches 1Mb new log file will be created.
         */
        fun size(size: Long) = apply {

            if (size > fileSize) {
                this.fileSize = size
            } else {
                Timber.d("File size should be more than 1Mb. Resetting to default capacity(1Mb)")
            }
        }

        fun build(): FileTree {

            if (!::fileDir.isInitialized) {
                throw IllegalArgumentException("Logs directory must be initialized...")
            }

            logWriter = LogWriter(fileDir)
            logWriter.maxSize = fileSize
            logWriter.init()
            return FileTree(logWriter)
        }
    }
}