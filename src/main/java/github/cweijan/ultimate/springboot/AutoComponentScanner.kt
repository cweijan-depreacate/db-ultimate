package github.cweijan.ultimate.springboot

import github.cweijan.ultimate.annotation.TableComponentScan
import github.cweijan.ultimate.component.ComponentScan
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.type.AnnotationMetadata

class AutoComponentScanner : ImportBeanDefinitionRegistrar {

    override fun registerBeanDefinitions(importingClassMetadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {

        val annotationAttributes = importingClassMetadata.getAnnotationAttributes(TableComponentScan::class.java.name)

        AnnotationAttributes.fromMap(annotationAttributes)?.getStringArray("value")?.run{
            ComponentScan.scan(this.asList())
        }

    }

}
