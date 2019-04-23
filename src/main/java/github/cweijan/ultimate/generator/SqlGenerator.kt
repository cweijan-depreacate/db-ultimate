package github.cweijan.ultimate.generator

import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.core.Query
import java.lang.reflect.Field

interface SqlGenerator {

    fun generateInsertSql(component: Any): String

    @Throws(IllegalAccessException::class)
    fun generateUpdateSql(component: Any): String

    fun <T> generateUpdateSql(componentInfo: ComponentInfo, query: Query<T>): String

    fun <T> generateDeleteSql(componentInfo: ComponentInfo, query: Query<T>): String

    fun <T> generateSelectSql(componentInfo: ComponentInfo, query: Query<T>): String

    fun <T> generateCountSql(componentInfo: ComponentInfo, query: Query<T>): String

}
