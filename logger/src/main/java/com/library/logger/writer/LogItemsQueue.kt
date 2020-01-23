package com.library.logger.writer

import java.util.concurrent.LinkedBlockingQueue

class LogItemsQueue<T>: LinkedBlockingQueue<T>() {

    var queueChangedListener = {

    }

    override fun put(e: T) {
        super.put(e)

        queueChangedListener()
    }
}