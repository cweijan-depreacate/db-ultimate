package github.cweijan.ultimate.core.generator

import github.cweijan.ultimate.core.generator.impl.MysqlGenerator
import github.cweijan.ultimate.core.generator.impl.OracleGenerator
import github.cweijan.ultimate.core.generator.impl.PostgresqlGenerator
import github.cweijan.ultimate.generator.DriverConstant

/**
 * 根据DB配置获取sql生成器
 */
object GeneratorAdapter {

    fun getSqlGenerator(driverName: String?): SqlGenerator {
        return when (driverName) {
            DriverConstant.MYSQL_DRIVER_NAME -> MysqlGenerator()
            DriverConstant.ORACLE_DRIVER_NAME -> OracleGenerator()
            DriverConstant.POSTGRESQL_DRIVER_NAME -> PostgresqlGenerator()
            DriverConstant.SQLITE_DRIVER_NAME -> PostgresqlGenerator()
            else -> MysqlGenerator()
        }
    }

}
