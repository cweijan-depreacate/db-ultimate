package github.cweijan.ultimate.test.bean

import github.cweijan.ultimate.annotation.Primary
import github.cweijan.ultimate.annotation.Table
import github.cweijan.ultimate.core.lucene.type.LuceneDocument

@Table(alias = "l")
@LuceneDocument(value=["id","message","test","msd"])
class Lib {

    @Primary
    var id: Int? = null
    var message: String? = null
    var test: String? = null
    var msd:String?=null
    var list:List<String>?=null;
    override fun toString(): String {
        return "Lib(id=$id, message=$message, test=$test, msd=$msd)"
    }

}

