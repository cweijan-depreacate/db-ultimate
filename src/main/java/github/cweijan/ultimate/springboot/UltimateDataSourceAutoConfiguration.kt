package github.cweijan.ultimate.springboot

import github.cweijan.ultimate.db.config.DbConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
@EnableConfigurationProperties(DbConfig::class)
open class UltimateDataSourceAutoConfiguration {

    @Autowired(required = false)
    private val dbConfig: DbConfig? = null

    @ConditionalOnMissingBean(DataSource::class)
    @Bean
    open fun createDataSource(): DataSource? {

        dbConfig?:return null
        if (dbConfig.configCheck())
            return dbConfig.dataSource!!

        return null
    }

}