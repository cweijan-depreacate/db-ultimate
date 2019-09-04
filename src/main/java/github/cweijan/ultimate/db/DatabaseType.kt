package github.cweijan.ultimate.db

/**
 * @author cweijan
 * @version 2019/6/28/028 11:04
 */
enum class DatabaseType {
    Mysql, Oracle, Postgresql, Sqllite, None;

    companion object{
        @JvmStatic
        fun getDatabaseType(url:String?): DatabaseType {
            return when {
                url?.indexOf("jdbc:mysql") != -1 -> Mysql
                url.indexOf("jdbc:oracle") != -1 -> Oracle
                url.indexOf("jdbc:postgresql") != -1 -> Mysql
                url.indexOf("jdbc:sqlite") != -1 -> Sqllite
                else -> None
            }
        }
    }
}


