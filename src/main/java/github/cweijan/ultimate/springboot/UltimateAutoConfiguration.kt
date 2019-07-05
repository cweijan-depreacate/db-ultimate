package github.cweijan.ultimate.springboot

import github.cweijan.ultimate.core.DbUltimate
import github.cweijan.ultimate.core.Query
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.util.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
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
    @Order(1)
    open fun createTransactionManager(): PlatformTransactionManager? {

        if (configCheck())
            return DataSourceTransactionManager(dataSource ?: dbConfig!!.dataSource!!)
        Log.info("please add \n" +
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration,org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration" +
                "\nto application.properties")
        return null
    }

    @Bean
    @Order(2)
    open fun createUltimate(): DbUltimate? {

        if (configCheck()){
            dataSource?.let {
                Log.debug("use datasource init dbultimate..")
                dbConfig!!.dataSource = it
            }
            Query.init(dbConfig!!)
            return Query.db
        }
        return null
    }

    private fun configCheck(): Boolean {

        return when {
            null == dbConfig || !dbConfig.enable -> {
                Log.info("db-ultimate is disabled, skip..")
                false
            }
            dbConfig.url == null -> {
                Log.error("jdbc url property not found! skip..")
                false
            }
            dbConfig.driver == null -> {
                Log.error("jdbc driver name property not found! skip..")
                false
            }
            dbConfig.username == null -> {
                Log.error("jdbc username property not found! skip..")
                false
            }
            dbConfig.password == null -> {
                Log.error("jdbc password property not found! skip..")
                false
            }
            else -> true
        }

    }


}
