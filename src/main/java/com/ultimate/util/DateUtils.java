package com.ultimate.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DateUtils{

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static DateFormat defaultDateFormat;

    private static DateFormat getDefaultDateFormat(){

        if(defaultDateFormat == null){
            defaultDateFormat = new SimpleDateFormat(PATTERN);
        }
        return defaultDateFormat;
    }

    /**
     将字符串解析为Date类型,格式为"yyyy-MM-dd HH:mm:ss"
     @param dateString  要解析的字符串
     */
    public static Date parseDate(String dateString){

        try{
            return getDefaultDateFormat().parse(dateString);
        } catch(ParseException e){
            Log.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

    /**
     将字符串解析为Date类型
     @param date 要解析的字符串
     @param pattern 字符串的日期格式
     */
    public static Date parseDate(String date,String pattern){
        try{
            return new SimpleDateFormat(pattern).parse(date);
        } catch(ParseException e){
            Log.getLogger().error(e.getMessage(), e);
        }
        return null;

    }

    /**
     获取n天之前的日期
     @param intervalDay n天
     */
    public static String getIntervalDay(int intervalDay){

        return getIntervalDay(intervalDay, "yyyy-MM-dd");
    }

    /**
     获取n天之前的日期
     @param intervalDay n天
     @param pattern n天之前日期的格式
     */
    public static String getIntervalDay(int intervalDay, String pattern){

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, intervalDay);

        return new SimpleDateFormat(pattern).format(calendar.getTime());
    }

    /**
     @return 返回当前时间 格式:yyyy-MM-dd HH:mm:ss
     */
    public static String now(){

        return getIntervalDay(0, PATTERN);
    }

    /**
     @param pattern 日期格式
     @return 返回当前时间,格式根据pattern
     */
    public static String now(String pattern){

        return getIntervalDay(0, pattern);
    }

    /**
     将日期转为字符串格式
     @param date 要转换的日期
     @return 转换后的字符串
     */
    public static String formatDate(Date date){

        Objects.requireNonNull(date);

        return getDefaultDateFormat().format(date);
    }

    /**
     将日期转为字符串格式
     @param date 要转换的日期
     @param pattern 转换的格式
     @return 转换后的字符串
     */
    public static String formatDate(Date date, String pattern){

        Objects.requireNonNull(date);

        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     @return 返回时间戳 格式:yyyyMMddHHmmss
     */
    public static String timestamp(){

        return getIntervalDay(0, "yyyyMMddHHmmssSSS");
    }

}
