package github.cweijan.ultimate.springboot;

import github.cweijan.ultimate.annotation.TableScan;
import github.cweijan.ultimate.core.component.ComponentScan;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author cweijan
 * @version 2019/9/3 18:55
 */
public class AutoComponentScanner implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(TableScan.class.getName());

        AnnotationAttributes tableScanAttributes = AnnotationAttributes.fromMap(annotationAttributes);
        if (tableScanAttributes != null) {
            List<String> value = Arrays.asList(tableScanAttributes.getStringArray("value"));
            ComponentScan.scan(value);
        }

    }

}
