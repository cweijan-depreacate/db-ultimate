package github.cweijan.ultimate.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val PATTERN = "yyyy-MM-dd HH:mm:ss"
    private val defaultDateFormat by lazy {
        return@lazy SimpleDateFormat(PATTERN);
    }

    /**
     * 将字符串解析为Date类型
     * @param date 要解析的字符串
     * @param pattern 字符串的日期格式
     */
    @JvmOverloads
    @JvmStatic
    fun parseDate(date: String, pattern: String = PATTERN): Date? {

        return if (pattern == PATTERN) {
            return defaultDateFormat.parse(date)
        } else SimpleDateFormat(pattern).parse(date)

    }

    /**
     * 将日期转为字符串格式
     * @param date 要转换的日期
     * @param pattern 转换的格式
     * @return 转换后的字符串
     */
    @JvmOverloads
    @JvmStatic
    fun formatDate(date: Date, pattern: String = PATTERN): String {
        return if (pattern == PATTERN) {
            return defaultDateFormat.format(date)
        } else SimpleDateFormat(pattern).format(date)
    }

    /**
     * 获取n天之前的日期
     * @param intervalDay n天
     * @param pattern n天之前日期的格式
     */
    @JvmOverloads
    @JvmStatic
    fun getIntervalDay(intervalDay: Int, pattern: String = "yyyy-MM-dd"): String {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, intervalDay)

        return SimpleDateFormat(pattern).format(calendar.time)
    }

    /**
     * @param pattern 日期格式
     * @return 返回当前时间,格式根据pattern
     */
    @JvmOverloads
    @JvmStatic
    fun now(pattern: String = PATTERN): String {

        return getIntervalDay(0, pattern)
    }

    /**
     * @return 返回时间戳 格式:yyyyMMddHHmmss
     */
    @JvmStatic
    fun timestamp(): String {

        return getIntervalDay(0, "yyyyMMddHHmmssSSS")
    }

}