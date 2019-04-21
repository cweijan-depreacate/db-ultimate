package github.cweijan.ultimate.test.bean

import github.cweijan.ultimate.annotation.*
import java.time.LocalDateTime

import java.util.Date

@Table(value="rh_admin",selectColumns = "*",alias = "ad",camelcaseToUnderLine = true)
class Admin {

    @Primary
    @ForeignKey(value=Lib::class)
    var id: Int = 0

    @Column
    private var message: String? = null

    @Column("msd")
    var msd:String?=null

    var helloWorldTest:String?=null

    @Column("create_date")
    var date: LocalDateTime? = null

//        @ExcludeResult
    var test: String? = null

    var lib: Lib? = null

    override fun toString(): String {
        return "Admin(id=$id, message=$message, msd=$msd, helloWorldTest=$helloWorldTest, date=${date}, test=$test, lib=$lib)"
    }

}
