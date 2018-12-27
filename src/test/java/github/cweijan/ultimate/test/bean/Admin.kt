package github.cweijan.ultimate.test.bean

import github.cweijan.ultimate.annotation.Column
import github.cweijan.ultimate.annotation.Exclude
import github.cweijan.ultimate.annotation.Primary
import github.cweijan.ultimate.annotation.Table

import java.util.Date

@Table(value="rh_admin",selectColumns = "*",alias = "ad")
class Admin {

    @Primary
    var id: Int = 0

    @Column
    var message: String? = null

    @Column("create_date")
    var date: Date? = null

    //    @Exclude
    var test: String? = null

    @Exclude
    var lib: Lib? = null

    override fun toString(): String {

        return "Admin{" + "id=" + id + ", message='" + message + '\''.toString() + ", date=" + date + ", test='" + test + '\''.toString() + '}'.toString()
    }
}
