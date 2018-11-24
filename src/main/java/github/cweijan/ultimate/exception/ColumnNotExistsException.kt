package github.cweijan.ultimate.exception

/**
 * 当获取CoponentInfo里面不存在的ColumnInfo时抛出此异常
 */
class ColumnNotExistsException(message: String) : RuntimeException(message)