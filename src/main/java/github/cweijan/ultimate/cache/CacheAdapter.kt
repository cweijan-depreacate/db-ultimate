package github.cweijan.ultimate.cache

import github.cweijan.ultimate.cache.impl.StandCacheImpl

object CacheAdapter {
    fun getCacheEngine(cacheType: String?=null): CacheEngine{
        return when (cacheType) {
            else -> StandCacheImpl()
        }
    }
}