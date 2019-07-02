package github.cweijan.ultimate.debug

import github.cweijan.ultimate.core.component.ComponentScan
import github.cweijan.ultimate.core.component.info.ComponentInfo
import github.cweijan.ultimate.db.config.DbConfig
import github.cweijan.ultimate.db.init.DBInitialer
import github.cweijan.ultimate.db.init.generator.TableAutoMode.create
import github.cweijan.ultimate.db.init.generator.TableAutoMode.update
import github.cweijan.ultimate.util.Log
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor
import java.io.File

class ClassReconfig(private val baseClasspath: String,val dbconfig:DbConfig) : FileAlterationListenerAdaptor() {

    private val initialer= DBInitialer(dbconfig)

    override fun onFileChange(file: File) {

        if(!file.path.endsWith(".class"))return
        val className = file.path.substring(baseClasspath.length + 1).replace("\\", ".").replace(".class", "")
        val componentClass = Class.forName(className)
        componentClass?: return
        if(ComponentScan.isComponent(componentClass)){
            val componentInfo = ComponentInfo.init(componentClass, false)
            when(dbconfig.tableMode){
                create ->initialer.createTable(componentInfo)
                update ->initialer.recreateTable(componentInfo)
                else -> ""
            }
            Log.debug("load component $className")
        }

    }

    override fun onFileCreate(file: File) {
        onFileChange(file)
    }

}