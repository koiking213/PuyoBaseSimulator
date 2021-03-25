package com.example.puyo_base_simulator.ui.home

data class FieldPreview(val id: Int,
                        val seed: Int,
                        val allClear: Boolean,
                        val numOfPlacement: Int,
                        val point: Int,
                        val content: String)