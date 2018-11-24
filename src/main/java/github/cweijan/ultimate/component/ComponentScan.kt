package github.cweijan.ultimate.component

import github.cweijan.ultimate.annotation.Table
import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.StringUtils

import java.io.File
import java.io.IOException
import java.net.URL
import java.util.ArrayList
import java.util.Enumeration
import java.util.Optional

/**
 * 扫描实体类
 */
object ComponentScan {

    private val alreadyScanPackages = ArrayList<String>()

    @JvmStatic
    fun scan(vararg scanPackages: String) {

        for (scanPackage in scanPackages) {
            scanTableClasses(scanPackage)
            alreadyScanPackages.add(scanPackage)
        }
    }

    private fun isComponent(clazz: Class<*>): Boolean {

        val table = clazz.getAnnotation(Table::class.java)

        return null != table
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     */
    fun scanTableClasses(packageName: String) {

        if (StringUtils.isEmpty(packageName) || alreadyScanPackages.contains(packageName)) {
            return
        }
        Log.logger.debug("scan component classes for package $packageName")
        val classLoader = Thread.currentThread().contextClassLoader
        val path = packageName.replace('.', '/')
        val resources: Enumeration<URL>
        val dirs = ArrayList<File>()
        try {
            resources = classLoader.getResources(path)
            while (resources.hasMoreElements()) {
                val resource = resources.nextElement()
                dirs.add(File(resource.file))
            }
        } catch (e: IOException) {
            Log.logger.error(e.message, e)
        }

        val classes = ArrayList<Class<*>>()
        for (directory in dirs) {
            classes.addAll(findClasses(directory, packageName))
        }
    }

    /**
     * Recursive method used to find all classes in a given directory and sub dirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     */
    private fun findClasses(directory: File, packageName: String): List<Class<*>> {

        val classes = ArrayList<Class<*>>()
        val files = Optional.ofNullable(directory.listFiles()).orElse(arrayOfNulls(0))

        for (file in files) {
            if (file.isDirectory) {
                classes.addAll(findClasses(file, packageName + "." + file.name))
                alreadyScanPackages.add(packageName + "." + file.name)
            } else if (file.name.endsWith(".class")) {
                val className = packageName + '.'.toString() + file.name.substring(0, file.name.length - 6)
                val clazz: Class<*>
                try {
                    clazz = Class.forName(className)
                } catch (e: ClassNotFoundException) {
                    Log.logger.error("fail load $className!")
                    continue
                }

                if (isComponent(clazz)) {
                    classes.add(clazz)
                    ComponentInfo.init(clazz)
                }
            }
        }

        return classes
    }

}
