package github.cweijan.ultimate.convert;

import java.util.Arrays;
import java.util.List;

/**
 * @author cweijan
 * @version 2019/9/5 12:34
 */
public class JavaType {
    public static final String Integer = "java.lang.Integer";
    public static final String Short = "java.lang.Short";
    public static final String Float = "java.lang.Float";
    public static final String Double = "java.lang.Double";
    public static final String Long = "java.lang.Long";
    public static final String Byte = "java.lang.Byte";
    public static final String byteArray = "[B";
    public static final String ByteArray = "[Ljava.lang.Byte;";
    public static final String Character = "java.lang.Character";
    public static final String String = "java.lang.String";
    public static final String Boolean = "java.lang.Boolean";
    public static final List<String> DATE_TYPE= Arrays.asList("java.time.LocalDateTime", "java.util.Date");
    public static final List<String> BYTE_ARRAY_TYPE=Arrays.asList(byteArray, ByteArray);
}
