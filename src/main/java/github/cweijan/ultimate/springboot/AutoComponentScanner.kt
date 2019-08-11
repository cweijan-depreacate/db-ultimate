package github.cweijan.ultimate.springboot

import github.cweijan.ultimate.annotation.TableScan
import github.cweijan.ultimate.core.component.ComponentScan
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.type.AnnotationMetadata

class AutoComponentScanner : ImportBeanDefinitionRegistrar {

    override fun registerBeanDefinitions(importingClassMetadata: AnnotationMetadata, registry: BeanDefinitionRegistry) {

        val annotationAttributes = importingClassMetadata.getAnnotationAttributes(TableScan::class.java.name)

        AnnotationAttributes.fromMap(annotationAttributes)?.getStringArray("value")?.run{
            ComponentScan.scan(this.asList())
//            ServiceScan.scan(registry,this.asList());
        }

    }

}
