package github.cweijan.ultimate.component

import github.cweijan.ultimate.annotation.Table
import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.util.Log
import github.cweijan.ultimate.util.StringUtils

import java.io.File
import java.util.ArrayList

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

    fun isComponent(clazz: Class<*>): Boolean {

        val table = ComponentInfo.getComponentClass(clazz)

        return null != table
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     */
    private fun scanTableClasses(packageName: String) {

        if (StringUtils.isEmpty(packageName) || alreadyScanPackages.contains(packageName)) {
            return
        }
        val resources = Thread.currentThread().contextClassLoader.getResources(packageName.replace('.', '/'))
        while (resources.hasMoreElements()) {
            val resource = resources.nextElement()
            Log.debug("scan component classes for path ${resource.path}")
            findClasses(File(resource.file), packageName)
        }

    }

    /**
     * Recursive method used to find all classes in a given directory and sub dirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     */
    private fun findClasses(directory: File, packageName: String) {

        val files = directory.listFiles() ?: arrayOfNulls(0)

        for (file in files) {
            if (file.isDirectory) {
                alreadyScanPackages.add("$packageName.${file.name}")
                findClasses(file, "$packageName.${file.name}");
            } else if (file.name.endsWith(".class")) {
                try {
                    val clazz = Class.forName("$packageName.${file.name.substring(0, file.name.length - 6)}")
                    if (isComponent(clazz)) {
                        ComponentInfo.init(clazz)
                    }
                } catch (e: ClassNotFoundException) {
                    Log.error("fail load $packageName.${file.name.substring(0, file.name.length - 6)}!")
                    continue
                }
            }
        }

    }

}
