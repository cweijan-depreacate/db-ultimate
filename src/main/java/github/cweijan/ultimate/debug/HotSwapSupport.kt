package github.cweijan.ultimate.debug

import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.db.init.DBInitialer
import org.apache.commons.io.monitor.FileAlterationMonitor
import org.apache.commons.io.monitor.FileAlterationObserver

import java.io.File
import java.util.concurrent.TimeUnit

object HotSwapSupport {

    fun startHotSwapListener(dbConfig: DbConfig) {

        try {
            val directory = File(Thread.currentThread().contextClassLoader.getResource("")!!.toURI())

            val observer = FileAlterationObserver(directory)
            val monitor = FileAlterationMonitor(TimeUnit.SECONDS.toMillis(5), observer)
            observer.addListener(ClassReconfig(directory.path, dbConfig))
            monitor.start()
        } catch (e: Exception) {
//            e.printStackTrace()
            println("offset monitor fail, break hotswap!")
        }

    }
}
