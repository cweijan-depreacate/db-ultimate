package github.cweijan.ultimate.db.init

import github.cweijan.ultimate.db.DatabaseType
import github.cweijan.ultimate.db.init.generator.TableInitSqlGenerator
import github.cweijan.ultimate.db.init.generator.impl.mysql.MysqlInit
import github.cweijan.ultimate.db.init.generator.impl.oracle.OracleInit
import github.cweijan.ultimate.db.init.generator.impl.postgresql.PostgresqlInit

/**
 * 根据DB配置获取sql生成器
 */
object GeneratorAdapter {

    fun getInitGenerator(databaseType: DatabaseType): TableInitSqlGenerator{
        return when (databaseType) {
            DatabaseType.Mysql -> MysqlInit()
            DatabaseType.Oracle -> OracleInit()
            DatabaseType.Postgresql -> PostgresqlInit()
            DatabaseType.Sqllite -> PostgresqlInit()
            else -> MysqlInit()
        }
    }

}
