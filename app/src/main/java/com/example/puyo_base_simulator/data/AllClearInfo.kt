package com.example.puyo_base_simulator.data

class AllClearInfo () {
    private val content = Array(5) {listOf<Field>()}
    fun add(num: Int, fields: List<Field>) {
        assert(num < 4)
        content[num] = fields
    }
    fun get(num: Int) : List<Field> {
        assert(num < 4)
        return content[num]
    }
}