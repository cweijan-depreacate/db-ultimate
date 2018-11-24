package github.cweijan.ultimate.db

import java.sql.SQLException

/**
 * 用于执行sql的lambda操作
 */
interface SqlWrapper {

    @Throws(SQLException::class)
    fun execute()

}
