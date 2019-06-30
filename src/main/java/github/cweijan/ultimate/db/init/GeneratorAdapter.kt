package github.cweijan.ultimate.db.init

import github.cweijan.ultimate.db.DatabaseType
import github.cweijan.ultimate.db.init.generator.TableInitSqlGenerator
import github.cweijan.ultimate.db.init.generator.TableStruct
import github.cweijan.ultimate.db.init.generator.impl.mysql.MysqlInit
import github.cweijan.ultimate.db.init.generator.impl.mysql.MysqlTableStruct
import github.cweijan.ultimate.db.init.generator.impl.oracle.OracleInit
import github.cweijan.ultimate.db.init.generator.impl.postgresql.PostgresqlInit

/**
 * 根据DB配置获取sql生成器
 */
object GeneratorAdapter {

    fun getInitGenerator(databaseType: DatabaseType): TableInitSqlGenerator{
        return when (databaseType) {
            DatabaseType.mysql -> MysqlInit()
            DatabaseType.oracle -> OracleInit()
            DatabaseType.postgresql -> PostgresqlInit()
            DatabaseType.sqllite -> PostgresqlInit()
            else -> MysqlInit()
        }
    }

}
