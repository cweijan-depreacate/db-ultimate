package github.cweijan.ultimate.convert

object JavaType {
    const val Integer = "java.lang.Integer"
    const val Short = "java.lang.Short"
    const val Float = "java.lang.Float"
    const val Double = "java.lang.Double"
    const val Long = "java.lang.Long"
    const val Byte = "java.lang.Byte"
    const val byteArray = "[B"
    const val ByteArray = "[Ljava.lang.Byte;"
    const val Character = "java.lang.Character"
    const val String = "java.lang.String"
    const val Boolean = "java.lang.Boolean"
    val DATE_TYPE: MutableList<String> = mutableListOf("java.time.LocalDateTime", "java.util.Date")
    val BYTE_ARRAY_TYPE: MutableList<String> = mutableListOf(byteArray, ByteArray)
}