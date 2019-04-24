package github.cweijan.ultimate.cache

interface CacheEngine {

    fun <T> get(key: String): T?

    fun <T> getAndReCache(key: String): T?

    /**
     * @param key    cache键
     * @param value  cache值
     * @param expireSecond 过期时间
     */
    fun set(key: String, value: Any, expireSecond: Int? = 30 * 60)

    fun remove(key: String)

    fun removeForPrefix(prefix: String)

    fun expire(key: String, expireSecond: Int)

    fun exists(key: String): Boolean

}
