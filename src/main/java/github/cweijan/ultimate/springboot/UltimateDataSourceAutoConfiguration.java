package github.cweijan.ultimate.springboot;

import github.cweijan.ultimate.db.HikariDataSourceAdapter;
import github.cweijan.ultimate.db.config.DbConfig;
import github.cweijan.ultimate.util.Log;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author cweijan
 * @version 2019/9/3 18:50
 */
@Configuration
@EnableConfigurationProperties(DbConfig.class)
@ConditionalOnProperty(value = "ultimate.jdbc.enable",havingValue = "true",matchIfMissing = true)
public class UltimateDataSourceAutoConfiguration {

    private  DbConfig dbConfig;

    public UltimateDataSourceAutoConfiguration(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    @Bean(name = "ultimateDatasource")
    @ConditionalOnMissingBean({DataSource.class})
    public DataSource createDataSource() {

        if (this.dbConfig.configCheck(null)) {
            Log.info("Not dataSource found! init hikariDataSource");
            return new HikariDataSourceAdapter(dbConfig).getDataSource();
        }

        return null;
    }
}
