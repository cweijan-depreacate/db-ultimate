package github.cweijan.ultimate.db.config

import github.cweijan.ultimate.cache.CacheAdapter
import org.springframework.boot.context.properties.ConfigurationProperties
import sun.misc.Cache

@ConfigurationProperties(prefix = "ultimate.cache")
class CacheConfig {
    var disableAllCache:Boolean=false
    var autoUseCache:Boolean=false
    var expireSecond:Int=30*60
    var cacheType:String=CacheAdapter.STAND
}