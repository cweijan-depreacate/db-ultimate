package github.cweijan.ultimate.springboot;

import github.cweijan.ultimate.annotation.TableComponentScan;
import github.cweijan.ultimate.component.ComponentScan;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

public class AutoComponentScanner implements ImportBeanDefinitionRegistrar{

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry){

        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(TableComponentScan.class.getName());

        if(annotationAttributes != null){
            String[] scanPackages = AnnotationAttributes.fromMap(annotationAttributes).getStringArray("value");
            ComponentScan.scan(scanPackages);
        }

    }

}
