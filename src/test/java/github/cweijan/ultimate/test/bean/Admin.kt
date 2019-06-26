package github.cweijan.ultimate.test.bean

import github.cweijan.ultimate.annotation.*
import github.cweijan.ultimate.annotation.query.Search
import github.cweijan.ultimate.core.excel.ExcludeExcel
import github.cweijan.ultimate.test.code.AdminTypeEnum
import java.time.LocalDateTime

@Table(value = "rh_admin", selectColumns = "*", alias = "ad", camelcaseToUnderLine = true)
open class Admin {

    @Primary
    @ForeignKey(value = Lib::class)
    var id: Int = 0

    @Column
    @Search("msd")
    var message: String? = null

    var adminType:AdminTypeEnum?=null

    @Column("message3")
    open var msd: String? = null

    @Column(length = 30)
    var newColumn:String?=null

    var helloWorldTest: String? = null

    @Column("create_date")
    var date: LocalDateTime? = null

//    @ExcludeResult
    var test: String? = null

    var isDelete:Boolean?=false

    @ExcludeExcel
    var lib: Lib? = null

    override fun toString(): String {
        return "Admin(id=$id, message=$message,  helloWorldTest=$helloWorldTest, date=$date)"
    }

}
