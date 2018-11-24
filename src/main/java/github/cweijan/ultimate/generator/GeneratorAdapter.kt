package github.cweijan.ultimate.generator

import github.cweijan.ultimate.generator.impl.MysqlGenerator
import github.cweijan.ultimate.db.config.DbConfig

import java.util.Objects

/**
 * 根据DB配置获取sql生成器
 */
class GeneratorAdapter(config: DbConfig) {

    private val driverName: String = config.driver!!

    val generator: SqlGenerator?
        get() {
            return if ("com.mysql.jdbc.Driver" == driverName) {
                MysqlGenerator()
            } else null
        }

}
