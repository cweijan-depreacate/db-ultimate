package github.cweijan.ultimate.annotation

import github.cweijan.ultimate.springboot.AutoComponentScanner
import org.springframework.context.annotation.Import

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Import(AutoComponentScanner::class)
@Retention(AnnotationRetention.RUNTIME)
annotation class TableComponentScan( val value: Array<String> = [""])
