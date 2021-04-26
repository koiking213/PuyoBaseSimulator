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
        undoButton.isEnabled = !isFirst()
        redoButton.isEnabled = true
        seekBar.progress--
        return ret
    }

    override fun undoAll(): T {
        val ret = super.undoAll()
        undoButton.isEnabled = false
        redoButton.isEnabled = !isLast()
        seekBar.progress =  0
        return ret
    }

    override fun redo() : T? {
        val ret = super.redo()
        undoButton.isEnabled = true
        redoButton.isEnabled = !isLast()
        seekBar.progress++
        return ret
    }

    override fun redoAll(): T {
        val ret = super.redoAll()
        undoButton.isEnabled = !isFirst()
        redoButton.isEnabled = false
        seekBar.progress = seekBar.max
        return ret
    }

    override fun clear() {
        super.clear()
        seekBar.max = 0
        undoButton.isEnabled = false
        redoButton.isEnabled = false
    }

    override fun set(idx: Int): Boolean {
        return if (super.set(idx)) {
            seekBar.progress = index
            undoButton.isEnabled = !isFirst()
            redoButton.isEnabled = !isLast()
            true
        } else false
    }

    init {
        seekBar.max = 0
        undoButton.isEnabled = false
        redoButton.isEnabled = false
    }
}