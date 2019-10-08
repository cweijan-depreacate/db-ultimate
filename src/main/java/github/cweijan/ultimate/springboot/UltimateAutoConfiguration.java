
package github.cweijan.ultimate.springboot;

import github.cweijan.ultimate.core.DbUltimate;
import github.cweijan.ultimate.db.config.DbConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * @author cweijan
 * @version 2019/9/3 18:42
 */
@Configuration
@AutoConfigureAfter({UltimateDataSourceAutoConfiguration.class})
@ConditionalOnBean({DataSource.class})
@EnableConfigurationProperties({DbConfig.class})
public class UltimateAutoConfiguration {

    private final DbConfig dbConfig;
    private final DataSource dataSource;

    public UltimateAutoConfiguration(DbConfig dbConfig,DataSource dataSource) {
        this.dbConfig = dbConfig;
        this.dataSource = dataSource;
    }

    @Bean(name = "ultimateTransactionManager")
    public PlatformTransactionManager ultimateTransactionManager() {
        return this.dbConfig.configCheck(dataSource) ? new DataSourceTransactionManager(this.dataSource) : null;
    }

    @Bean
    public DbUltimate initUltimate(){
        return DbUltimate.init(dbConfig,dataSource);
    }

    @Bean
    @ConditionalOnMissingBean({TransactionTemplate.class,DataSourceTransactionManager.class})
    public TransactionTemplate transactionTemplate() {
        return new TransactionTemplate() {
            public void afterPropertiesSet() {
            }
        };
    }

}
