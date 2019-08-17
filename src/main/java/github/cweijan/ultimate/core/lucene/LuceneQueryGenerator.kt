package github.cweijan.ultimate.core.lucene

import github.cweijan.ultimate.util.StringUtils

/**
 * @author cweijan
 * @version 2019/7/16/016 20:29
 */
object LuceneQueryGenerator {

    @JvmStatic
    fun <T> generateOperationSql(query: LuceneQuery<T>): String {

        val and = "AND"
        val or = "OR"
        val not = "NOT"
        var queryString = ""

        if (StringUtils.isNotBlank(query.searchFullContent)) queryString = "$and ${query.searchFullContent} "
        if (query.searchLazy.isInitialized()) queryString += generateOperationSql0(query.searchOperation, "", and)
        if (query.orEqLazy.isInitialized()) queryString += generateOperationSql0(query.orEqualsOperation, "", or)
        if (query.notEqualsLazy.isInitialized()) queryString += generateOperationSql0(query.notEqualsOperation, "", not)

        if (queryString.startsWith(not)) {
            queryString = queryString.replaceFirst(not.toRegex(), "*:* $and $not")
        }

        if (queryString.startsWith(and)) {
            queryString = queryString.replaceFirst(and.toRegex(), "")
        }
        if (queryString.startsWith(or)) {
            queryString = queryString.replaceFirst(or.toRegex(), "")
        }

        if (StringUtils.isEmpty(queryString)) queryString = "*:*"
        return queryString
    }

    private fun generateOperationSql0(operationMap: Map<String, List<Any>>?, condition: String, separator: String): String {

        val tempQueryString = StringBuilder()

        operationMap?.forEach { key, operations ->
            operations.forEach { value ->
                tempQueryString.append("$separator $key:${condition + value} ")
            }
        }

        return tempQueryString.toString()
    }

}
