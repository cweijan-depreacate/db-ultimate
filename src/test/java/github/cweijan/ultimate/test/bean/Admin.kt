package github.cweijan.ultimate.test.bean

import github.cweijan.ultimate.annotation.*

import java.util.Date

@Table(value="rh_admin",selectColumns = "*",alias = "ad",camelcaseToUnderLine = true)
class Admin {

    @Primary
    @ForeignKey(Lib::class)
    var id: Int = 0

    @Column
    private var message: String? = null

    var msd:String?=null

    var helloWorldTest:String?=null

    @Column("create_date")
    var date: Date? = null

    //    @Exclude
    var test: String? = null

    @Exclude
    var lib: Lib? = null

    override fun toString(): String {
        return "Admin(id=$id, message=$message, msd=$msd, helloWorldTest=$helloWorldTest, date=$date, test=$test, lib=$lib)"
    }

}
