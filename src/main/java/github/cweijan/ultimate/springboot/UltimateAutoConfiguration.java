package github.cweijan.ultimate.springboot;

import github.cweijan.ultimate.core.DbUltimate;
import github.cweijan.ultimate.db.config.DbConfig;
import github.cweijan.ultimate.util.Log;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnClass(DbUltimate.class)
@EnableConfigurationProperties(DbConfig.class)
public class UltimateAutoConfiguration{

    @Autowired
    private DbConfig dbConfig;

    @Bean
    public DbUltimate createUltimate(){

        return new DbUltimate(dbConfig);
    }

    @Configuration
    @Import({AutoComponentScanner.class})
    public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean{

        @Override
        public void afterPropertiesSet(){

            Log.getLogger().info("InitializingBean");
        }
    }

}
