package github.cweijan.ultimate.springboot

import github.cweijan.ultimate.core.DbUltimate
import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.util.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@EnableConfigurationProperties(DbConfig::class)
open class UltimateAutoConfiguration {

    @Autowired(required = false)
    private val dbConfig: DbConfig? = null
    @Autowired(required = false)
    private val dataSource: DataSource? = null

    @Bean
    open fun createTransactionManager(): PlatformTransactionManager? {

        dbConfig?:return null
        if (dbConfig.configCheck())
            return DataSourceTransactionManager(dataSource ?: dbConfig.dataSource!!)
        Log.info("please add \n" +
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration,org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration" +
                "\nto application.properties")
        return null
    }

    @Bean
    open fun createUltimate(): DbUltimate? {

        dbConfig?:return null
        if (dbConfig.configCheck()){
            dataSource?.let {
                Log.debug("use datasource init dbultimate..")
                dbConfig.dataSource = it
            }
            Query.init(dbConfig)
            return Query.db
        }
        return null
    }

}
