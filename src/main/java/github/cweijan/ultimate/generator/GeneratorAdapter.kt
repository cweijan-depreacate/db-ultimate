package github.cweijan.ultimate.generator

import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.generator.impl.MysqlGenerator
import github.cweijan.ultimate.generator.impl.OracleGenerator
import github.cweijan.ultimate.generator.impl.PostgresqlGenerator

/**
 * 根据DB配置获取sql生成器
 */
class GeneratorAdapter(config: DbConfig) {

    private val driverName: String? = config.driver

    val generator: SqlGenerator
        get() {

            return when (driverName) {
                DriverConstant.MYSQL_DRIVER_NAME -> MysqlGenerator()
                DriverConstant.ORACLE_DRIVER_NAME -> OracleGenerator()
                DriverConstant.POSTGRESQL_DRIVER_NAME -> PostgresqlGenerator()
                DriverConstant.SQLITE_DRIVER_NAME -> PostgresqlGenerator()
                else ->MysqlGenerator()
            }
        }

}
