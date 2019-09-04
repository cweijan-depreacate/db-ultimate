package github.cweijan.ultimate.annotation;

import github.cweijan.ultimate.springboot.AutoComponentScanner;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cweijan
 * @version 2019/9/3 19:13
 */
@Target(ElementType.TYPE)
@Import(AutoComponentScanner.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableScan {
    String[] value();
}
