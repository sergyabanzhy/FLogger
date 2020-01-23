package com.library.logger

import timber.log.Timber
import java.io.File

fun File.getMbSize(): Long {
    return length()/1024*1024
}

fun File.getDigitsFromName(): Int {

    val regex = Regex("[a-zA-Z]")
    val index = name.replace(regex, "").replace(".", "")

    return Integer.parseInt(index)
}

fun FileTree.Companion.getLogFiles(block:(Array<File>) -> (Unit)) {
    for (three in Timber.forest()) {
        when(three) {
            is FileTree -> {
                three.getLogFiles(block)
            }
        }
    }
}