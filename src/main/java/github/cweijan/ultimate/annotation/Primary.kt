package github.cweijan.ultimate.annotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Primary(val autoIncrement: Boolean = true)
