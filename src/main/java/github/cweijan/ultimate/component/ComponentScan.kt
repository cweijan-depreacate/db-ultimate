package github.cweijan.ultimate.component

import github.cweijan.ultimate.component.info.ComponentInfo
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
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

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     */
    private fun scanTableClasses(packageName: String) {

        val classLoadersList = LinkedList<ClassLoader>()
        classLoadersList.add(ClasspathHelper.contextClassLoader())
        classLoadersList.add(ClasspathHelper.staticClassLoader())

        val reflections = Reflections(ConfigurationBuilder()
                .setScanners(SubTypesScanner(false), ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(*classLoadersList.toTypedArray()))
                .filterInputsBy(FilterBuilder().include(FilterBuilder.prefix(packageName))))
        reflections.getSubTypesOf(Any::class.java).forEach {
            if (ComponentInfo.getComponentClass(it) != null) ComponentInfo.init(it)
        }

    }
}