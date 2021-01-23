package com.example.puyo_base_simulator.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.puyo_base_simulator.R
import com.example.puyo_base_simulator.data.AppDatabase
import com.example.puyo_base_simulator.data.Base
import com.example.puyo_base_simulator.ui.home.Haipuyo.searchSeedWithPattern
import java.util.*
import java.util.stream.Collectors

class LoadFieldPopup(private val context: Context) : PopupWindow(context) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadFieldAdapter: LoadFieldAdapter
    private lateinit var listener: (Int, FieldPreview) -> Unit

    // should be initialized in Presenter?
    private var mDB: AppDatabase = Room.databaseBuilder(context,
            AppDatabase::class.java, "database-name")
            .allowMainThreadQueries()
            .build()

    fun setFieldSelectedListener(fieldSelectedListener: (Int, FieldPreview) -> Unit) {
        listener = fieldSelectedListener
        loadFieldAdapter.setFieldSelectedListener(fieldSelectedListener)
    }

    private fun setupView() {
        @SuppressLint("InflateParams") val view = LayoutInflater.from(context).inflate(R.layout.popup_load, null)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(context, GridLayoutManager.VERTICAL))
        recyclerView.addItemDecoration(DividerItemDecoration(context, GridLayoutManager.HORIZONTAL))
        loadFieldAdapter = LoadFieldAdapter(ArrayList())
        recyclerView.adapter = loadFieldAdapter
        val searchBySeedButton = view.findViewById<Button>(R.id.searchBySeedButton)
        searchBySeedButton.setOnClickListener {
            val editText = view.findViewById<EditText>(R.id.seedEditTextNumberDecimal)
            try {
                val seed = editText.text.toString().toInt()
                searchBySeed(seed)
            } catch (e: NumberFormatException) {
                editText.error = e.message
            }
        }
        val searchByPatternButton = view.findViewById<Button>(R.id.searchByPatternButton)
        searchByPatternButton.setOnClickListener {
            val editText = view.findViewById<EditText>(R.id.patternEditText)
            val str = editText.text.toString()
            searchByPattern(str)
        }
        val showAllButton = view.findViewById<Button>(R.id.showAllButton)
        showAllButton.setOnClickListener {
            showAll()
        }
        contentView = view
    }

    private fun searchBySeed(seed : Int) {
        if (seed !in 0..65535) {
            throw NumberFormatException("should enter 0-65535")
        }
        val bases = mDB.baseDao().findByHash(seed)
        val fieldPreviews = bases.map {FieldPreview(it.id, seed, it.field)}
        loadFieldAdapter = LoadFieldAdapter(fieldPreviews)
        loadFieldAdapter.setFieldSelectedListener(listener)
        recyclerView.adapter = loadFieldAdapter
    }

    private fun searchByPattern(pattern : String) {
        val seeds = searchSeedWithPattern(pattern)
        val seedsChunks = seeds.chunked(100)
        val bases = seedsChunks.parallelStream().map { seed: List<Int> -> mDB.baseDao().findByAllHash(seed) }.flatMap { obj: List<Base> -> obj.stream() }.collect(Collectors.toList())
        val fieldPreviews = bases.map {FieldPreview(it.id, it.hash, it.field)}
        loadFieldAdapter = LoadFieldAdapter(fieldPreviews)
        loadFieldAdapter.setFieldSelectedListener(listener)
        recyclerView.adapter = loadFieldAdapter

    }

    private fun showAll () {
        val bases = mDB.baseDao().all
        val fieldPreviews = bases.map {FieldPreview(it.id, it.hash, it.field)}
        loadFieldAdapter = LoadFieldAdapter(fieldPreviews)
        loadFieldAdapter.setFieldSelectedListener(listener)
        recyclerView.adapter = loadFieldAdapter
    }

    init {
        setupView()
    }
}