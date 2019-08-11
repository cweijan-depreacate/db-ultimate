package github.cweijan.ultimate.test.bean

import github.cweijan.ultimate.annotation.Column
import github.cweijan.ultimate.annotation.OneToOne
import github.cweijan.ultimate.annotation.Primary
import github.cweijan.ultimate.annotation.Table
import github.cweijan.ultimate.annotation.query.Search
import github.cweijan.ultimate.core.excel.ExcludeExcel
import github.cweijan.ultimate.test.code.AdminTypeEnum
import java.time.LocalDateTime

@Table(value = "rh_admin",  alias = "ad")
open class Admin {

    @Primary
    @OneToOne(relationClass = Lib::class)
    var id: Int = 0

    @Column
    @Search("msd")
    var message: String? = null

    var adminType:AdminTypeEnum?=null

    @Column("message3",nullable = true)
    open var msd: String? = null

    @Column(length = 30)
    @ExcludeExcel
    var newColumn:String?=null

    var helloWorldTest: String? = null

    @Column("create_date",excelHeader = "创建时间")
    var date: LocalDateTime? = null

//    @ExcludeResult
    var test: String? = null

    var isDelete:Boolean?=false

    override fun toString(): String {
        return "Admin(id=$id, message=$message,  helloWorldTest=$helloWorldTest, date=$date)"
    }

}
