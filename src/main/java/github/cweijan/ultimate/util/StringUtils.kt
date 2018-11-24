package github.cweijan.ultimate.util

/**
 * 字符串操作工具类
 */
object StringUtils {

    @JvmStatic
    fun isEmpty(string: String?): Boolean {

        val strLen = string?.length ?: 0
        if (string == null || strLen == 0) {
            return true
        }
        for (i in 0 until strLen) {
            if (!Character.isWhitespace(string[i])) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun isNotEmpty(string: String?): Boolean {

        return !isEmpty(string)
    }

}
