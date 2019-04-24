package github.cweijan.ultimate.cache

import github.cweijan.ultimate.cache.impl.RedisCacheImpl
import github.cweijan.ultimate.cache.impl.StandCacheImpl
import github.cweijan.ultimate.db.config.CacheConfig

object CacheAdapter {
    const val STAND = "stand"
    const val REDIS = "redis"

    fun getCacheEngine(cacheConfig: CacheConfig? = null): CacheEngine {
        return when (cacheConfig?.cacheType) {
            STAND -> StandCacheImpl()
            REDIS -> RedisCacheImpl()
            else -> StandCacheImpl()
        }
    }
}