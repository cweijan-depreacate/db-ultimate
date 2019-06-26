package github.cweijan.ultimate.core.component

import github.cweijan.ultimate.core.component.info.ComponentInfo
import github.cweijan.ultimate.springboot.util.ServiceInject
import github.cweijan.ultimate.util.ClassTools
import org.springframework.beans.factory.support.RootBeanDefinition
//import org.reflections.Reflections
//import org.reflections.scanners.ResourcesScanner
//import org.reflections.scanners.SubTypesScanner
//import org.reflections.util.ClasspathHelper
//import org.reflections.util.ConfigurationBuilder
//import org.reflections.util.FilterBuilder
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

//        val classLoadersList = LinkedList<ClassLoader>()
//        classLoadersList.add(ClasspathHelper.contextClassLoader())
//        classLoadersList.add(ClasspathHelper.staticClassLoader())
//
//        val reflections = Reflections(ConfigurationBuilder()
//                .setScanners(SubTypesScanner(false), ResourcesScanner())
//                .setUrls(ClasspathHelper.forClassLoader(*classLoadersList.toTypedArray()))
//                .filterInputsBy(FilterBuilder().include(FilterBuilder.prefix(packageName))))
//        reflections.getSubTypesOf(Any::class.java).forEach { if (isComponent(it)) ComponentInfo.init(it) }

    }
}