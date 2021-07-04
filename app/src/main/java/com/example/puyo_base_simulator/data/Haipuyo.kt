package com.example.puyo_base_simulator.data

import com.example.puyo_base_simulator.BuildConfig
import java.io.BufferedReader
import java.io.IOException
import java.util.*

object Haipuyo {
    private var content: MutableList<String> = ArrayList()
    private var sortedContent: MutableList<String> = ArrayList()
    fun load(haipuyoBr: BufferedReader, sortedBr: BufferedReader) {
        try {
            for (i in 0..65535) {
                content.add(haipuyoBr.readLine())
                sortedContent.add(sortedBr.readLine())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    operator fun get(seed: Int): String {
        if (BuildConfig.DEBUG && seed !in 0..65535) {
            throw AssertionError("Assertion failed")
        }
        return content[seed]
    }

    fun searchSeedWithPattern(str: String): List<Int> {
        val len = str.length
        if (BuildConfig.DEBUG && len % 2 != 0) {
            throw AssertionError("Assertion failed")
        }
        val sortedStr = pairwiseSort(str)
        val ret: MutableList<Int> = ArrayList()
        for (i in 0..65535) {
            if (sortedContent[i].startsWith(sortedStr)) {
                ret.add(i)
            }
        }
        return ret
    }

    private fun pairwiseSort(str: String): String {
        var newStr = ""
        for (i in 0 until str.length / 2) {
            val substring = str.substring(i * 2, i * 2 + 2)
            val chars = substring.toCharArray()
            Arrays.sort(chars)
            val sorted = String(chars)
            newStr += sorted
        }
        return newStr
    }
}