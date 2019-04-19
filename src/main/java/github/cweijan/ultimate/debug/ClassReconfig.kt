package github.cweijan.ultimate.debug

import github.cweijan.ultimate.component.ComponentScan
import github.cweijan.ultimate.component.TableInfo
import github.cweijan.ultimate.component.info.ComponentInfo
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.db.init.DBInitialer
import github.cweijan.ultimate.util.Log
import org.apache.commons.io.monitor.FileAlterationListener
import org.apache.commons.io.monitor.FileAlterationObserver

import java.io.File

class ClassReconfig(private val baseClasspath: String,val dbconfig:DbConfig) : FileAlterationListener {

    val initialer=DBInitialer(dbconfig)

    override fun onFileChange(file: File) {

        if(!file.path.endsWith(".class"))return
        val className = file.path.substring(baseClasspath.length + 1).replace("\\", ".").replace(".class", "")
        val componentClass = Class.forName(className)
        componentClass?: return
        if(ComponentScan.isComponent(componentClass)){
            val componentInfo = ComponentInfo.init(componentClass, false)
            if(dbconfig.createNonexistsTable){
                initialer.createTable(componentInfo)
            }
            Log.debug("reload component $className")
        }

    }

    override fun onFileCreate(file: File) {

        if(!file.path.endsWith(".class"))return
        val className = file.path.substring(baseClasspath.length + 1).replace("\\", ".").replace(".class", "")
        val componentClass = Class.forName(className)
        componentClass?: return
        if(ComponentScan.isComponent(componentClass)){
            val componentInfo = ComponentInfo.init(componentClass, false)
            if(dbconfig.createNonexistsTable){
                initialer.createTable(componentInfo)
            }
            Log.debug("init component $className")
        }
    }



    override fun onFileDelete(file: File) {
        if(!file.path.endsWith(".class"))return
        val className = file.path.substring(baseClasspath.length + 1).replace("\\", ".").replace(".class", "")
        TableInfo.removeComponent(Class.forName(className))
        Log.debug("remove component $className")
    }

    override fun onStart(fileAlterationObserver: FileAlterationObserver) {
    }

    override fun onStop(fileAlterationObserver: FileAlterationObserver) {
    }

    override fun onDirectoryCreate(file: File) {
    }

    override fun onDirectoryChange(file: File) {
    }

    override fun onDirectoryDelete(file: File) {
    }

}