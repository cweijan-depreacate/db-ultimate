package github.cweijan.ultimate.debug

import org.apache.commons.io.monitor.FileAlterationMonitor
import org.apache.commons.io.monitor.FileAlterationObserver

import java.io.File
import java.net.URI
import java.util.concurrent.TimeUnit

object HotSwapSupport {

    fun startHotSwapListener() {

        val directory = File(Thread.currentThread().contextClassLoader.getResource("")!!.toURI())

        val observer = FileAlterationObserver(directory)
        val monitor = FileAlterationMonitor(TimeUnit.SECONDS.toMillis(5), observer)
        observer.addListener(ClassReconfig(directory.path))
        monitor.start()

    }
}
