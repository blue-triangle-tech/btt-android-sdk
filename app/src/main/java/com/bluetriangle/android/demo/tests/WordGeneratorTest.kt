package com.bluetriangle.android.demo.tests

import java.lang.StringBuilder

class WordGeneratorTest(val word:String = "unknown", val interval: Long = 10L):BTTTestCase {
    override val title: String
        get() = "Generate Word"
    override val description: String
        get() = "Tries to generate word \"$word\" for $interval secs"

    private val alphabets = "abcdefghijklmnopqrstuvwxyz"

    override fun run(): String? {
        val startTime = System.currentTimeMillis()
        val result = arrayListOf<Char>()
        for(c in word) {
            result.add('-')
        }
        generateWord(word, result.toTypedArray(), 0, startTime, interval)
        return word
    }

    private fun generateWord(
        word: String,
        result:Array<Char>,
        index: Int,
        startTime: Long,
        interval: Long
    ):Boolean {
        if(index == word.length) return resultEqualsWord(result, word)

        for(alphabet in alphabets) {
            if(System.currentTimeMillis() - startTime > (interval * 1000)) return false
            result[index] = alphabet
            if(generateWord(word, result, index+1, startTime, interval)) return true
            result[index] = '-'
        }
        return false
    }


    private fun resultEqualsWord(result: Array<Char>, word: String): Boolean {
        for(i in word.indices) {
            if(result[i] != word[i]) return false
        }
        return true
    }
}