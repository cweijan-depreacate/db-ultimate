package github.cweijan.ultimate.annotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Column
(val value: String = "", val nullable: Boolean = false, val length: Int = 0)
