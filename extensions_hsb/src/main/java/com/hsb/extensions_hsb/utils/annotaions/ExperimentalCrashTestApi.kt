package com.hsb.extensions_hsb.utils.annotaions

/**
 * Developed by Syed Haseeb
 * syedhaseeb1.github.io
 */

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This API is experimental and unsafe. The core functionality if this API is to fill the ram and cause OutOfMemoryException Or ANR. Use with caution and proper planning."
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class ExperimentalCrashTestApi
