package github.cweijan.ultimate.cache

import github.cweijan.ultimate.cache.impl.StandCacheImpl
import github.cweijan.ultimate.db.config.CacheConfig

object CacheAdapter {
    fun getCacheEngine(cacheConfig: CacheConfig?=null): CacheEngine{
        return when (cacheConfig) {
            else -> StandCacheImpl()
        }
    }
}