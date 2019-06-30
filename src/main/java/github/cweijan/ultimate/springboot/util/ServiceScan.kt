package github.cweijan.ultimate.springboot.util

import github.cweijan.ultimate.util.ClassTools
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.RootBeanDefinition

object ServiceScan {

    fun scan(registry: BeanDefinitionRegistry, packageList: List<String>) {
        packageList.forEach { packageName ->

            ClassTools.getClasses(packageName).forEach { c ->
                if (ServiceInject::class.java.isAssignableFrom(c) && !c.isAnonymousClass && c!=ServiceInject::class.java)
                    registry.registerBeanDefinition(c.name, RootBeanDefinition(c))
            }

        }

    }


}
