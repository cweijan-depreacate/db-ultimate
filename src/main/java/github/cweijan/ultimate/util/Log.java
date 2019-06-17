package github.cweijan.ultimate.util;

import kotlin.jvm.JvmStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {

    private static Logger loggerChecker = LoggerFactory.getLogger(Log.class);

    public static Logger getLogger(){

        return getLoggerInner();
    }

    private static Logger getLoggerInner() {
        StackTraceElement stackTrace = (new Throwable()).getStackTrace()[2];
        return LoggerFactory.getLogger(stackTrace.getClassName());
    }

    @JvmStatic
    public static void error(Object content) {
        if (loggerChecker.isErrorEnabled()) {
            getLoggerInner().error(content != null ? content.toString() : null, "");
        }

    }

    @JvmStatic
    public static void error(Object content, Throwable throwable) {
        if (loggerChecker.isErrorEnabled()) {
            getLoggerInner().error(content != null ? content.toString() : null, "", throwable);
        }

    }

    @JvmStatic
    public static void warn(Object content) {
        if (loggerChecker.isWarnEnabled()) {
            getLoggerInner().warn(content != null ? content.toString() : null, "");
        }

    }

    @JvmStatic
    public static void debug(Object content) {
        if (loggerChecker.isDebugEnabled()) {
            getLoggerInner().debug(content != null ? content.toString() : null, "");
        }

    }

    @JvmStatic
    public static void info(Object content) {
        if (loggerChecker.isInfoEnabled()) {
            getLoggerInner().info(content != null ? content.toString() : null, "");
        }

    }

    private Log() {
    }
}
