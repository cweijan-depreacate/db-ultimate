package com.ultimate.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log{

    /**
     @return 返回slf4j logger对象
     */
    public static Logger getLogger(){

        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];

        String invokeClassName = stackTrace.getClassName();

        return LoggerFactory.getLogger(invokeClassName);
    }
}
