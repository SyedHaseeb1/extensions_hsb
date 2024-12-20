package com.hsb.extensions_hsb.utils.stringExtensions

import java.util.Locale

/**
 * Developed by Syed Haseeb
 * syedhaseeb1.github.io
 */

object StringExtensions {

    fun String.takeRandom(numOfChar: Int): String {
        val startIndex = indices.random()
        val result = StringBuilder()
        for (i in 0 until minOf(numOfChar, length - startIndex)) {
            result.append(this[(startIndex + i) % length])
        }
        return result.toString()
    }

    fun String.capitalizeFirstLetter(): String {
        if (isEmpty()) return this
        return this.substring(0, 1).toUpperCase() + this.substring(1)
    }

    fun String.capitalizeFirstLetterOnly(): String {
        if (isEmpty()) return this
        return this.substring(0, 1).uppercase(Locale.US) + this.substring(1).lowercase(Locale.US)
    }

    fun String.replaceCharacters(): String {
        val regex = "[^a-zA-Z0-9 ]".toRegex()
        return replace(regex, "")
    }

    fun String.short() = if (this.length > 30) {
        this.substring(0, 30).trim()
    } else {
        this.trim()
    }

    fun String.shortBy(max: Int) = if (this.length > max) {
        this.substring(0, max).trim()
    } else {
        this.trim()
    }

}