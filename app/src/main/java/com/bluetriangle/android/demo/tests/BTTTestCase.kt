package com.bluetriangle.android.demo.tests

interface BTTTestCase {

    val title: String

    val description: String

    fun run(): String?

}