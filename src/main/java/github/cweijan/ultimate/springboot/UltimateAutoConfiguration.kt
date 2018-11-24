package github.cweijan.ultimate.springboot

import github.cweijan.ultimate.core.DbUltimate
import github.cweijan.ultimate.db.config.DbConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnClass(DbUltimate::class)
@EnableConfigurationProperties(DbConfig::class)
open class UltimateAutoConfiguration {

    @Autowired
    private val dbConfig: DbConfig? = null

    @Bean
    open fun createUltimate(): DbUltimate {

        return DbUltimate(dbConfig!!)
    }

}
