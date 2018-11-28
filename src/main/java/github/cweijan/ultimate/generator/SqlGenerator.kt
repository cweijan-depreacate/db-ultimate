package github.cweijan.ultimate.generator

import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.core.Operation

interface SqlGenerator {

    fun generateInsertSql(component: Any): String

    @Throws(IllegalAccessException::class)
    fun generateUpdateSql(component: Any): String

    fun <T> generateUpdateSql(componentInfo: ComponentInfo, operation: Operation<T>): String

    fun <T> generateDeleteSql(componentInfo: ComponentInfo, operation: Operation<T>): String

    fun <T> generateSelectSql(componentInfo: ComponentInfo, operation: Operation<T>): String

    fun <T> generateCountSql(componentInfo: ComponentInfo, operation: Operation<T>): String
}
