package github.cweijan.ultimate.exception

/**
 * 当获取TableInfo里面不存在的Component时抛出此异常
 */
class ComponentNotExistsException(message: String) : RuntimeException(message)
