
package github.cweijan.ultimate.springboot;

import github.cweijan.ultimate.core.DbUltimate;
import github.cweijan.ultimate.db.config.DbConfig;
import github.cweijan.ultimate.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @author cweijan
 * @version 2019/9/3 18:42
 */
@Configuration
@AutoConfigureAfter(UltimateDataSourceAutoConfiguration.class)
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties({DbConfig.class})
@ConditionalOnProperty(value = "ultimate.jdbc.enable",havingValue = "true",matchIfMissing = true)
public class UltimateAutoConfiguration {

    private final DbConfig dbConfig;

    public UltimateAutoConfiguration(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Bean(name = "ultimateTransactionManager")
    public PlatformTransactionManager ultimateTransactionManager(DataSource dataSource) {
        return this.dbConfig.configCheck(dataSource) ? new DataSourceTransactionManager(dataSource) : null;
    }

    @Bean
    public DbUltimate initUltimate(DataSource dataSource){
        return DbUltimate.init(dbConfig,dataSource);
    }

}
