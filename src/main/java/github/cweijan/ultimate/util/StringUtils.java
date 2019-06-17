package github.cweijan.ultimate.util;

public class StringUtils {

    /**
     * Check string is blank
     */
    public static boolean isBlank(String string) {
        if (isEmpty(string)) return true;
        for (int i = 0; i < string.length(); i++) {
            if (!Character.isWhitespace(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check string is not blank
     */
    public static boolean isNotBlank(String string) {
        return !isBlank(string);
    }


    /**
     * Check string is blank
     *
     * @param string To verify string
     * @return If string is empty
     */
    public static boolean isEmpty(String string) {
        return string == null || "".equals(string);
    }

    /**
     * check string is not empty
     */
    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }

    private StringUtils() {
    }

}
