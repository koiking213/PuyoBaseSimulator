package com.example.puyo_base_simulator.ui.home

import android.widget.Button
import android.widget.SeekBar
import java.util.*

// TODO: ボタンは将来的になくす
// シークバーをあわせたときにplacementも更新される必要がある？ -> 離した時でいい気がする
class SeekableHistory<T> internal constructor(private val seekBar: SeekBar,
                                           private val undoButton: Button,
                                           private val redoButton: Button) : History <T>() {

    override fun add(elm: T) {
        super.add(elm)
        seekBar.max = super.size() - 1
        seekBar.progress = super.size() - 1
        undoButton.isEnabled = !isFirst()
        redoButton.isEnabled = false
    }

    override fun undo() : T? {
        val ret = super.undo()
        redoButton.isEnabled = true
        undoButton.isEnabled = !isFirst()
        seekBar.progress--
        return ret
    }

    override fun undoAll(): T {
        val ret = super.undoAll()
        redoButton.isEnabled = !isLast()
        undoButton.isEnabled = false
        seekBar.progress =  0
        return ret
    }

    override fun redo() : T? {
        val ret = super.redo()
        redoButton.isEnabled = !isLast()
        undoButton.isEnabled = true
        seekBar.progress++
        return ret
    }

    override fun redoAll(): T {
        val ret = super.redoAll()
        redoButton.isEnabled = false
        undoButton.isEnabled = !isFirst()
        seekBar.progress = seekBar.max
        return ret
    }

    override fun clear() {
        super.clear()
        seekBar.max = 0
        undoButton.isEnabled = false
        redoButton.isEnabled = false
    }

    init {
        seekBar.max = 0
        undoButton.isEnabled = false
        redoButton.isEnabled = false
    }
}