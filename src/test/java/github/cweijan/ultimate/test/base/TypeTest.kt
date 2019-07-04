package github.cweijan.ultimate.test.base

import github.cweijan.ultimate.convert.TypeAdapter
import github.cweijan.ultimate.test.bean.CreateTest
import org.junit.Test

class p{
    var age:Int?=null
}

class TypeTest : BaseTest() {

    @Test
    fun testDefaultInt(){

        for (declaredField in p::class.java.declaredFields) {
            println(declaredField.type.name)
        }
    }

    @Test
    fun test(){

        var result = TypeAdapter.convertHumpToUnderLine(CreateTest::class.java.simpleName)
        println(result)

    }

}