package com.example.puyo_base_simulator.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
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
import com.example.puyo_base_simulator.ui.home.LoadFieldAdapter.FieldSelectedListener
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Function
import java.util.stream.Collectors

class LoadFieldPopup(private val context: Context) : PopupWindow(context) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadFieldAdapter: LoadFieldAdapter
    private lateinit var listener: (Int, FieldPreview) -> Unit

    // should be initialized in Presenter?
    var mDB: AppDatabase
    fun setFieldSelectedListener(fieldSelectedListener: (Int, FieldPreview) -> Unit) {
        listener = fieldSelectedListener
        loadFieldAdapter!!.setFieldSelectedListener(fieldSelectedListener)
    }

    private fun setupView() {
        @SuppressLint("InflateParams") val view = LayoutInflater.from(context).inflate(R.layout.popup_load, null)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false))
        recyclerView.addItemDecoration(DividerItemDecoration(context, GridLayoutManager.VERTICAL))
        recyclerView.addItemDecoration(DividerItemDecoration(context, GridLayoutManager.HORIZONTAL))
        loadFieldAdapter = LoadFieldAdapter(ArrayList())
        recyclerView.setAdapter(loadFieldAdapter)
        val searchBySeedButton = view.findViewById<Button>(R.id.searchBySeedButton)
        searchBySeedButton.setOnClickListener { v: View? ->
            val editText = view.findViewById<EditText>(R.id.seedEditTextNumberDecimal)
            try {
                val seed = editText.text.toString().toInt()
                if (!(0 <= seed && seed <= 65535)) {
                    throw NumberFormatException()
                }
                val bases = mDB.baseDao().findByHash(seed)
                val fieldPreviews: MutableList<FieldPreview> = ArrayList()
                for (base in bases) {
                    fieldPreviews.add(FieldPreview(base.id, seed, base.field))
                }
                loadFieldAdapter = LoadFieldAdapter(fieldPreviews)
                loadFieldAdapter!!.setFieldSelectedListener(listener)
                recyclerView.setAdapter(loadFieldAdapter)
            } catch (e: NumberFormatException) {
                editText.error = "should enter 0-65535."
            }
        }
        val searchByPatternButton = view.findViewById<Button>(R.id.searchByPatternButton)
        searchByPatternButton.setOnClickListener { v: View? ->
            val editText = view.findViewById<EditText>(R.id.patternEditText)
            val str = editText.text.toString()
            val seeds = searchSeedWithPattern(str)
            val counter = AtomicInteger()
            val seedsChunks = seeds.stream().collect(Collectors.groupingBy(Function { it: Int? -> counter.getAndIncrement() / 100 })).values
            val bases: Collection<Base> = seedsChunks.parallelStream().map { seed: List<Int> -> mDB.baseDao().findByAllHash(seed) }.flatMap { obj: List<Base> -> obj.stream() }.collect(Collectors.toList())
            val fieldPreviews: MutableList<FieldPreview> = ArrayList()
            for (base in bases) {
                fieldPreviews.add(FieldPreview(base.id, base.hash, base.field))
            }
            loadFieldAdapter = LoadFieldAdapter(fieldPreviews)
            loadFieldAdapter!!.setFieldSelectedListener(listener)
            recyclerView.setAdapter(loadFieldAdapter)
        }
        val showAllButton = view.findViewById<Button>(R.id.showAllButton)
        showAllButton.setOnClickListener { v: View? ->
            val bases = mDB.baseDao().all
            val fieldPreviews: MutableList<FieldPreview> = ArrayList()
            for (base in bases) {
                fieldPreviews.add(FieldPreview(base.id, base.hash, base.field))
            }
            loadFieldAdapter = LoadFieldAdapter(fieldPreviews)
            loadFieldAdapter!!.setFieldSelectedListener(listener)
            recyclerView.setAdapter(loadFieldAdapter)
        }
        contentView = view
    }

    init {
        mDB = Room.databaseBuilder(context,
                AppDatabase::class.java, "database-name")
                .allowMainThreadQueries()
                .build()
        setupView()
    }
}