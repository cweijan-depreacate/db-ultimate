package github.cweijan.ultimate.db.init.generator

/**
 * @author cwj
 * @date 2019/6/25/025 14:42
 */
abstract class BaseInitSqlGenerator : TableInitSqlGenerator {

    override fun generateCommentSqlFragment(comment: String): String? {
        return null
    }

}

