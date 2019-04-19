package github.cweijan.ultimate.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Log {

    private val loggerChecker = LoggerFactory.getLogger(Log::class.java)

    private val logger: Logger
        get() {
            val stackTrace = Throwable().stackTrace[2]
            val invokeClassName = stackTrace.className
            return LoggerFactory.getLogger(invokeClassName)
        }

    @JvmStatic
    fun error(content: Any?) {

        if (loggerChecker.isErrorEnabled) logger.error(content?.toString() + "")
    }

    @JvmStatic
    fun error(content: Any?, throwable: Throwable) {

        if (loggerChecker.isErrorEnabled) logger.error(content?.toString() + "", throwable)
    }

    @JvmStatic
    fun warn(content: Any?) {

        if (loggerChecker.isWarnEnabled) logger.warn(content?.toString() + "")
    }

    @JvmStatic
    fun debug(content: Any?) {

        if (loggerChecker.isDebugEnabled) logger.debug(content.toString() + "")

    }

    @JvmStatic
    fun info(content: Any?) {

        if (loggerChecker.isInfoEnabled) logger.info(content?.toString() + "")

    }

}