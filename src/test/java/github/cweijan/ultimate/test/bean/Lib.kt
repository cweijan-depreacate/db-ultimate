package github.cweijan.ultimate.test.bean

import github.cweijan.ultimate.annotation.Primary
import github.cweijan.ultimate.annotation.Table

@Table
class Lib {

    @Primary
    var id: Int? = null
    var message: String? = null
    var test: String? = null
}