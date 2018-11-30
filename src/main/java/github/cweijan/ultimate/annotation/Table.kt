package github.cweijan.ultimate.annotation

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class Table(val value: String = "", val selectColumns: String = "*")
