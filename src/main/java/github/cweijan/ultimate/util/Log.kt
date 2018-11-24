package github.cweijan.ultimate.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Log {

    /**
     * @return 返回slf4j logger对象
     */
    @JvmStatic
    val logger: Logger
        get() {
            val invokeClassName = Thread.currentThread().stackTrace[2].className
            return LoggerFactory.getLogger(invokeClassName)
        }
}
