package github.cweijan.ultimate.core.component

import github.cweijan.ultimate.core.component.info.ComponentInfo
import github.cweijan.ultimate.util.ClassTools
import java.util.*


/**
 * 扫描实体类
 */
object ComponentScan {

    private val alreadyScanPackages = ArrayList<String>()

    @JvmStatic
    fun scan(scanPackages: List<String>) {

        scanPackages.forEach { scanPackage ->
            scanTableClasses(scanPackage)
            alreadyScanPackages.add(scanPackage)
        }
    }

    @JvmStatic
    fun isComponent(clazz: Class<*>?): Boolean {
        if(clazz==null)return false

        val table = ComponentInfo.getComponentClass(clazz)

        return null != table
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     */
    private fun scanTableClasses(packageName: String) {

        ClassTools.getClasses(packageName).forEach { if (isComponent(it)) ComponentInfo.init(it)}

    }
}