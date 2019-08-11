package github.cweijan.ultimate.util

import github.cweijan.ultimate.convert.JavaType
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

object DateUtils {

    public const val DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss"
    private val formatCache by lazy {
        return@lazy HashMap<String, SimpleDateFormat>()
    }
    private val timeFormatCache by lazy {
        return@lazy HashMap<String, DateTimeFormatter>()
    }


    @JvmOverloads
    @JvmStatic
    fun toDateObject(dateString: String, dateType: Class<*>, dateFormat: String = DEFAULT_PATTERN): Any? {
        if (StringUtils.isBlank(dateString)) return null
        val resultTime = try {
            DateUtils.getDateFormat(dateFormat).parse(dateString)
        } catch (e: Exception) {
            Log.getLogger().error(e.message)
            return null
        }
        return when (dateType) {
            Date::class.java -> resultTime
            LocalDateTime::class.java -> LocalDateTime.ofInstant(resultTime.toInstant(), ZoneId.systemDefault())
            LocalDate::class.java -> LocalDateTime.ofInstant(resultTime.toInstant(), ZoneId.systemDefault()).toLocalDate()
            LocalTime::class.java -> LocalDateTime.ofInstant(resultTime.toInstant(), ZoneId.systemDefault()).toLocalTime()
            else -> null
        }
    }

    @JvmOverloads
    @JvmStatic
    fun toDateString(datevalue: Any, dateFormat: String = DEFAULT_PATTERN): String? {
        return when (datevalue::class.java.name) {
            Date::class.java.name -> DateUtils.getDateFormat(dateFormat).format((datevalue as Date))
            LocalDateTime::class.java.name -> (datevalue as LocalDateTime).format(DateUtils.getDateTimeFormatter(dateFormat))
            LocalDate::class.java.name -> (datevalue as LocalDate).format(DateUtils.getDateTimeFormatter(dateFormat))
            LocalTime::class.java.name -> (datevalue as LocalTime).format(DateUtils.getDateTimeFormatter(dateFormat))
            else -> null
        }
    }

    /**
     * 将字符串解析为Date类型
     * @param date 要解析的字符串
     * @param pattern 字符串的日期格式
     */
    @JvmOverloads
    @JvmStatic
    fun parseDate(date: String, pattern: String = DEFAULT_PATTERN): Date? {

        return getDateFormat(pattern).parse(date)

    }

    /**
     * 将日期转为字符串格式
     * @param date 要转换的日期
     * @param pattern 转换的格式
     * @return 转换后的字符串
     */
    @JvmOverloads
    @JvmStatic
    fun formatDate(date: Date, pattern: String = DEFAULT_PATTERN): String {

        return getDateFormat(pattern).format(date)
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

        return getDateFormat(pattern).format(calendar.time)
    }

    /**
     * @param pattern 日期格式
     * @return 返回当前时间,格式根据pattern
     */
    @JvmOverloads
    @JvmStatic
    fun now(pattern: String = DEFAULT_PATTERN): String {

        return getIntervalDay(0, pattern)
    }

    /**
     * @return 返回时间戳 格式:yyyyMMddHHmmss
     */
    @JvmStatic
    fun timestamp(): String {

        return (System.currentTimeMillis() / 1000).toString()
    }

    /**
     *
     * @return int 返回时间戳 (10位)
     */
    fun timestampForInt(): Int {

        return (System.currentTimeMillis() / 1000).toInt()
    }

    @JvmStatic
    fun getDateTimeFormatter(dateFormat: String): DateTimeFormatter {
        timeFormatCache[dateFormat]?.run { return this }
        timeFormatCache[dateFormat] = DateTimeFormatter.ofPattern(dateFormat)
        return timeFormatCache[dateFormat]!!
    }

    @JvmStatic
    fun getDateFormat(dateFormat: String): SimpleDateFormat {
        formatCache[dateFormat]?.run { return this }
        formatCache[dateFormat] = SimpleDateFormat(dateFormat)
        return formatCache[dateFormat]!!
    }

    fun convertDateToLong(dateValue: Any?): Long? {
        dateValue ?: return null
        return when (dateValue::class.java.name) {
            Date::class.java.name -> (dateValue as Date).time
            LocalDateTime::class.java.name -> (dateValue as LocalDateTime).atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli()
            else -> null
        }
    }

    fun convertLongToDate(timestamp: Long?, dateType: Class<*>): Any? {
        timestamp ?: return null
        if (!JavaType.DATE_TYPE.contains(dateType.name)) return null
        return when (dateType.name) {
            Date::class.java.name -> Date(timestamp)
            LocalDateTime::class.java.name -> Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
            else -> null
        }

    }

}