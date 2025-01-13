package com.dmuhia.foregroundserviceapp

import org.junit.Test

import org.junit.Assert.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val name = "Damaris"
       val name1 =  StringBuilder().append("Wambui")
        name1.append(name)
        println("Name is $name1")
        assertEquals(4, 2 + 2)
    }
}