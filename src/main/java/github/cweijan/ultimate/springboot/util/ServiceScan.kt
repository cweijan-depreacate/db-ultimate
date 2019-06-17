package github.cweijan.ultimate.springboot.util

import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.RootBeanDefinition
import java.util.*

object ServiceScan {

    fun scan(registry: BeanDefinitionRegistry, packageList: List<String>) {
        packageList.forEach { packageName ->

            val classLoadersList = LinkedList<ClassLoader>()
            classLoadersList.add(ClasspathHelper.contextClassLoader())
            classLoadersList.add(ClasspathHelper.staticClassLoader())

            val reflections = Reflections(ConfigurationBuilder()
                    .setScanners(SubTypesScanner(true), ResourcesScanner())
                    .setUrls(ClasspathHelper.forClassLoader(*classLoadersList.toTypedArray()))
                    .filterInputsBy(FilterBuilder().include(FilterBuilder.prefix(packageName))))
            reflections.getSubTypesOf(ServiceInject::class.java).forEach { c ->
                if(!c.isAnonymousClass)
                    registry.registerBeanDefinition(c.name, RootBeanDefinition(c))
            }

        }

    }


}
