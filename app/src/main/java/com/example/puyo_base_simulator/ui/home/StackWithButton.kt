package com.example.puyo_base_simulator.ui.home

import java.util.*

class StackWithButton<T> internal constructor(private val enableFun: () -> Unit,
                                              private val disableFun: () -> Unit) : Stack<T>() {
    override fun push(elm: T): T {
        enableFun.invoke()
        return super.push(elm)
    }

    override fun pop(): T {
        val elm = super.pop()
        if (super.isEmpty()) disableFun.invoke()
        return elm
    }

    override fun clear() {
        super.clear()
        disableFun.invoke()
    }

    init {
        disableFun.invoke()
    }
}