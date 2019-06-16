package github.cweijan.ultimate.util;

import kotlin.jvm.JvmStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {

    private static Logger loggerChecker = LoggerFactory.getLogger(Log.class);

    private static Logger getLogger() {
        StackTraceElement stackTrace = (new Throwable()).getStackTrace()[2];
        return LoggerFactory.getLogger(stackTrace.getClassName());
    }

    @JvmStatic
    public static void error(Object content) {
        if (loggerChecker.isErrorEnabled()) {
            getLogger().error(content != null ? content.toString() : null, "");
        }

    }

    @JvmStatic
    public static void error(Object content, Throwable throwable) {
        if (loggerChecker.isErrorEnabled()) {
            getLogger().error(content != null ? content.toString() : null, "", throwable);
        }

    }

    @JvmStatic
    public static void warn(Object content) {
        if (loggerChecker.isWarnEnabled()) {
            getLogger().warn(content != null ? content.toString() : null, "");
        }

    }

    @JvmStatic
    public static void debug(Object content) {
        if (loggerChecker.isDebugEnabled()) {
            getLogger().debug(content != null ? content.toString() : null, "");
        }

    }

    @JvmStatic
    public static void info(Object content) {
        if (loggerChecker.isInfoEnabled()) {
            getLogger().info(content != null ? content.toString() : null, "");
        }

    }

    private Log() {
    }
}
