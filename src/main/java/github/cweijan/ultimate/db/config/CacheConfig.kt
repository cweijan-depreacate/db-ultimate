package github.cweijan.ultimate.db.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ultimate.cache")
class CacheConfig {
    var disableAllCache:Boolean=false
    var autoUseCache:Boolean=false
    var expireSecond:Int=30*60
}