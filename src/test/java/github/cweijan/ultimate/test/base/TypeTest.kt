package github.cweijan.ultimate.test.base

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


    }

}