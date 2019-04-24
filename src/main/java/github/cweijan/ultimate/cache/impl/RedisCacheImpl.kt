package github.cweijan.ultimate.cache.impl

import github.cweijan.ultimate.cache.CacheEngine

class RedisCacheImpl:CacheEngine {
    override fun <T> get(key: String): T? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T> getAndReCache(key: String): T? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun set(key: String, value: Any?, expireSecond: Int?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(key: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeForPrefix(prefix: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeAll() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun expire(key: String, expireSecond: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun exists(key: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}