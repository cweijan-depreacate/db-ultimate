package github.cweijan.ultimate.test.bean

import github.cweijan.ultimate.annotation.Blob
import github.cweijan.ultimate.annotation.Column
import github.cweijan.ultimate.annotation.Primary
import github.cweijan.ultimate.annotation.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Table("test_init")
class CreateTest {

    @Primary(value = "id", autoIncrement = true, comment = "主键", length = 3)
    var id: Int? = null

    @Blob
    var magenetList:List<String>?=null

    @Column(value = "name1", comment = "姓名", defaultValue = "cwj", nullable = true, length = 30,unique = true)
    var name: String? = null

    @Column(value = "ag", comment = "年龄", defaultValue = "10", nullable = false, length = 30)
    var age: Int? = null

    @Column(length = 5,unique = true)
    var fl: Float? = null

    var d1: LocalDateTime? = null

    var d2: Date? = null

    @Column(nullable = true)
    var t1: LocalDate? = null

    var t2: LocalTime? = null

    @Column(length = 3)
    var doub: Double? = null

    @Column(nullable = false)
    var byte: Array<Byte>? = null

    var isC: Boolean? = null

    var lon: Long? = null

}