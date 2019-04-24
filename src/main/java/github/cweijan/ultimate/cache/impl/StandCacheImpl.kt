package github.cweijan.ultimate.cache.impl

import github.cweijan.ultimate.cache.CacheEngine
import github.cweijan.ultimate.util.DateUtils

import java.util.HashMap

/**
 * cache实现,主要对部门和jsapi_ticket进行缓存
 */
class StandCacheImpl : CacheEngine {
    private val cacheMap = HashMap<String, CacheEnity>()

    override fun removeAll() {
        cacheMap.clear()
    }

    override fun removeForPrefix(prefix: String) {
        cacheMap.keys.forEach { key -> if (key.startsWith(prefix)) cacheMap.remove(key) }
    }

    override fun expire(key: String, expireSecond: Int) {
        if (exists(key)) {
            cacheMap[key]!!.exipre = expireSecond
        }
    }

    override fun exists(key: String): Boolean {

        return cacheMap.containsKey(key) && (DateUtils.timestampForInt() - cacheMap[key]!!.cacheTime) < cacheMap[key]!!.exipre
    }

    override fun <T> get(key: String): T? {
        return if (exists(key)) {
            cacheMap[key]!!.data as T
        } else {
            null
        }
    }

    override fun <T> getAndReCache(key: String): T? {
        val cacheItem = get<T>(key)
        cacheItem?.let { cacheMap[key]!!.cacheTime = DateUtils.timestampForInt() }
        return cacheItem
    }

    override fun remove(key: String) {

        cacheMap.remove(key)
    }

    override fun set(key: String, value: Any?, expireSecond: Int?) {

        value?.run { cacheMap[key] = CacheEnity(value, expireSecond!!, DateUtils.timestampForInt()) }
    }

    private class CacheEnity(var data: Any, var exipre: Int, var cacheTime: Int)

}