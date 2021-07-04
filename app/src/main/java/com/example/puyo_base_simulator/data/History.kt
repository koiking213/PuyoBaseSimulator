package com.example.puyo_base_simulator.data

// TODO: Historyデータ構造についてもっとちゃんと考えたほうが良さそう(indexとcontent.sizeの関係とか、初期値とかhistory追加のタイミングとか)
// add (index=1, content.size=1)
// -> add (index=2, content.size=2)
// -> undo (index=1, content.size=2, return content[0])
// -> redo (index=2, content.size=2, return content[1])
// currentでcontent[index]を返す
open class History<T> () : Iterable<T> {
    private var content = mutableListOf<T>()
    var index = -1

    open fun add(elm: T) {
        content = content.subList(0, index+1)
        content.add(elm)
        index++
    }

    open fun undo() : T? = when {
        index <= 0 -> null
        else -> {
            index--
            content[index]
        }
    }

    open fun undoAll() : T {
        index = 0
        return content[0]
    }

    open fun redo() : T? = when {
        content.isEmpty() -> null
        index >= content.size - 1 -> null
        else -> {
            index++
            content[index]
        }
    }

    open fun redoAll() : T {
        index = content.size - 1
        return content[index]
    }

    open fun clear() {
        content.clear()
        index = -1
    }

    open fun set(idx: Int) : Boolean {
        return if (idx in 0 until content.size) {
            index = idx
            true
        } else false
    }

    fun isFirst() : Boolean = index == 0
    fun isLast() : Boolean = index == content.size - 1
    fun isEmpty() : Boolean = content.isEmpty()

    fun latest() : T = content.last()
    fun current() : T = content[index]
    fun previous() : T? = if (index <= 0) null else content[index-1]

    fun size() : Int = content.size

    override fun iterator(): Iterator<T> = content.iterator()

}