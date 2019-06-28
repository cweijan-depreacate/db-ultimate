package github.cweijan.ultimate.core.dialect

import github.cweijan.ultimate.core.dialect.impl.MysqlDialect
import github.cweijan.ultimate.core.dialect.impl.OracleDialect
import github.cweijan.ultimate.core.dialect.impl.PostgresqlDialect
import github.cweijan.ultimate.db.DatabaseType

/**
 * @author cweijan
 * @version 2019/6/28/028 11:15
 */
object DialectAdapter{

    fun getSqlGenerator(databaseType: DatabaseType): SqlDialect {
        return when (databaseType) {
            DatabaseType.mysql -> MysqlDialect()
            DatabaseType.oracle -> OracleDialect()
            DatabaseType.postgresql -> PostgresqlDialect()
            DatabaseType.sqllite -> PostgresqlDialect()
            else -> MysqlDialect()
        }
    }

}
