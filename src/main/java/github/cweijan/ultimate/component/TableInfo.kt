package github.cweijan.ultimate.component

import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.exception.ComponentNotExistsException

import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object TableInfo {

    private val TypeMap: MutableMap<String, ComponentInfo> by lazy {
        return@lazy HashMap<String, ComponentInfo>()
    }

    val componentList: MutableList<ComponentInfo> by lazy {
        return@lazy ArrayList<ComponentInfo>()
    }

    @JvmStatic
    fun putComponent(clazz: Class<*>, componentInfo: ComponentInfo) {
        TypeMap[clazz.name] = componentInfo
        componentList.add(componentInfo)
    }

    fun isAlreadyInit(clazz: Class<*>): Boolean {
        return TypeMap.containsKey(clazz.name)
    }

    fun getComponent(clazz: Class<*>): ComponentInfo {
        return TypeMap[clazz.name]
                ?: throw ComponentNotExistsException("$clazz component is not exists!")
    }

    fun getTableName(clazz: Class<*>): String? {
        return getComponent(clazz).tableName;
    }

}
