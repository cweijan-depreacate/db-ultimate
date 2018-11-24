package github.cweijan.ultimate.generator

import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.core.Operation

interface SqlGenerator {

    fun generateInsertSql(component: Any, selective: Boolean): String

    @Throws(IllegalAccessException::class)
    fun generateUpdateSql(component: Any): String

    fun generateUpdateSql(componentInfo: ComponentInfo, operation: Operation): String

    fun generateDeleteSql(componentInfo: ComponentInfo, operation: Operation): String

    fun generateSelectSql(componentInfo: ComponentInfo, operation: Operation): String

    fun generateCountSql(componentInfo: ComponentInfo, operation: Operation): String
}
