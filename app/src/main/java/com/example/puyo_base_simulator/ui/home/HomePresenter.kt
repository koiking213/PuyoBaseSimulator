package com.example.puyo_base_simulator.ui.home

import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import android.database.sqlite.SQLiteConstraintException
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.puyo_base_simulator.data.*
import com.example.puyo_base_simulator.data.room.AppDatabase
import com.example.puyo_base_simulator.data.room.Base
import com.example.puyo_base_simulator.data.room.SeedDatabase
import com.example.puyo_base_simulator.data.room.SeedEntity
import com.example.puyo_base_simulator.utils.Rotation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import java.util.stream.Collectors
import kotlin.math.roundToInt


class HomePresenter internal constructor(asset: AssetManager, dataStore: DataStore<Preferences>) : ViewModel() {
    var fieldHistory = History<Field>()
    private var tsumoController: TsumoController
    private var fieldDB: AppDatabase? = null
    private var seedDB: SeedDatabase? = null
    private var settingRepository: SettingRepository
    val emptyTsumoInfo : TsumoInfo
        get() = TsumoInfo(
        Array(2) { PuyoColor.EMPTY},
        Array(2) {Array(2) { PuyoColor.EMPTY}},
        3,
        Rotation.DEGREE0
        )
    private val _tsumoInfo = MutableLiveData(emptyTsumoInfo)
    val tsumoInfo: LiveData<TsumoInfo> = _tsumoInfo
    private val _currentField = MutableLiveData(Field())
    val currentField: LiveData<Field> = _currentField
    private val _seed = MutableLiveData(0)
    val seed: LiveData<Int> = _seed
    private val _historySliderValue = MutableLiveData(0f)
    val historySliderValue = _historySliderValue
    private val _historySize = MutableLiveData(0)
    val historySize = _historySize
    private val _duringChain = MutableLiveData(false)
    val duringChain = _duringChain
    private val _chainInfo = MutableLiveData(ChainInfo(0, 0, 0, 0, 0))
    val chainInfo = _chainInfo
    private val _allClearInfo = MutableLiveData(AllClearInfo())
    val allClearInfo: LiveData<AllClearInfo> = _allClearInfo
    private val _allClearLoading = MutableLiveData(false)
    val allClearLoading = _allClearLoading

    var chainSpeed: Long = 500

    fun fastenChainSpeed() {
        chainSpeed = 100
    }

    fun normalChainSpeed() {
        chainSpeed = 500
    }

    val showDoubleNext: StateFlow<Boolean>
        get() = settingRepository.showDoubleNextFlow.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun updateShowDoubleNext(show: Boolean) {
        viewModelScope.launch {
            settingRepository.updateShowDoubleNext(show)
        }
    }

    private fun getFieldDB(context: Context) : AppDatabase {
        if (fieldDB == null) {
            val result = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "database-name"
            ).allowMainThreadQueries()
                    .build()
            fieldDB = result
        }
        return fieldDB!!
    }

    private fun getSeedDB(context: Context) : SeedDatabase {
        val db = seedDB
        return if (db == null) {
            val result = Room.databaseBuilder(
                context.applicationContext,
                SeedDatabase::class.java, "seed-database"
            ).allowMainThreadQueries().build()
            seedDB = result
            result
        } else {
           db
        }
    }

    fun rotateLeft() {
        tsumoController.rotateCurrentRight()
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    fun rotateRight() {
        tsumoController.rotateCurrentLeft()
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    fun moveLeft() {
        tsumoController.moveCurrentLeft()
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    fun moveRight() {
        tsumoController.moveCurrentRight()
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    fun dropDown(activity: Activity) {
        val newField = currentField.value!!.setPairOnField(tsumoController.makeTsumoInfo()) ?: return
        tsumoController.addPlacementHistory()
        newField.evalNextField()
        _currentField.value = newField
        fieldHistory.add(getLastField(newField))
        _historySliderValue.value = fieldHistory.index.toFloat()
        _historySize.value = fieldHistory.size()
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
        if (newField.nextField != null) {
            Thread {
                chain(newField, activity)
            }.start()
        }
    }

    fun undo() {
        val f = fieldHistory.undo() ?: return
        tsumoController.undoPlacementHistory()
        _currentField.value = f
        _historySliderValue.value = fieldHistory.index.toFloat()
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    fun redo(activity: Activity) {
        fieldHistory.redo() ?: return
        _historySliderValue.value = fieldHistory.index.toFloat()
        val tsumoInfo = tsumoController.makeTsumoInfo(tsumoController.currentPlacementHistory())
        val field = currentField.value!!.setPairOnField(tsumoInfo)!!
        tsumoController.redoPlacementHistory()
        field.evalNextField()
        _currentField.value = field
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
        if (field.nextField != null) {
            Thread {
                chain(field, activity)
            }.start()
        }
    }

    fun save(context: Context) : Boolean {
        if (fieldHistory.isFirst()) return false
        val base = Base()
        base.hash = seed.value!!
        base.placementHistory = tsumoController.placementOrderToString()
        val field = currentField.value!!
        base.allClear = field.allClear()
        base.point = field.accumulatedPoint
        base.field = if (field.allClear()) {
            fieldHistory.previous().toString()
        } else {
            field.toString()
        }
        getFieldDB(context).baseDao().insert(base)
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
        return true
    }

    fun stockSeed(context: Context) : Boolean {
        val entity = SeedEntity()
        entity.seed = seed.value!!
        try {
            getSeedDB(context).seedDao().insert(entity)
        } catch (e: SQLiteConstraintException) {
            return false
        }
        return true
    }

    fun forgetSeed(context: Context, seed: Int) {
        val entity = SeedEntity()
        entity.seed = seed
        getSeedDB(context).seedDao().delete(entity)
    }

    fun stockedSeeds(context: Context) : List<Int>{
        return getSeedDB(context).seedDao().all.map {it.seed}
    }

    fun getTsumo(seed: Int, index: Int) : List<PuyoColor>{
        return TsumoController.getPair(Haipuyo[seed], index)
    }

    fun load(base: Base) {
        tsumoController = TsumoController(Haipuyo[base.hash], base.hash)
        clearFieldHistory()
        var f = Field()
        for (p in tsumoController.stringToPlacementOrder(base.placementHistory)) {
            f = f.setPairOnField(tsumoController.makeTsumoInfo(p))!!
            f.evalNextField()
            f = getLastField(f)
            fieldHistory.add(f)
        }
        tsumoController.addPlacementHistory()
        tsumoController.rollbackPlacementHistory()
        _currentField.value = fieldHistory.undoAll()
        _historySliderValue.value = fieldHistory.index.toFloat()
        _historySize.value = fieldHistory.size()
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
        _seed.value = base.hash // TODO: 名前がおかしいので修正する
    }

    private fun clearFieldHistory() {
        fieldHistory.clear()
        fieldHistory.add(Field())
        _historySliderValue.value = fieldHistory.index.toFloat()
        _historySize.value = fieldHistory.size()
    }

    private fun getLastField(field: Field): Field {
        val next = field.nextField
        return if (next == null) {
            field
        } else {
            getLastField(next)
        }
    }

    fun setSeed(newSeed: Int) {
        _seed.value = newSeed
        tsumoController = TsumoController(Haipuyo[newSeed], newSeed)
        _currentField.value = Field()
        clearFieldHistory()
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    fun generateByPattern(pattern: String) {
        val seeds = Haipuyo.searchSeedWithPattern(pattern)
        if (seeds.isNotEmpty()) {
            setSeed(seeds.random())
        }
    }

    fun randomGenerate() {
        _currentField.value = Field()
        clearFieldHistory()
        val seed = RANDOM.nextInt(65536)
        _seed.value = seed
        tsumoController = TsumoController(Haipuyo[seed], seed)
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    fun setHistoryIndex(idx: Float) {
        _historySliderValue.value = idx
        fieldHistory.index = idx.roundToInt()
        _currentField.value = fieldHistory.current()
        tsumoController.setHistoryIndex(idx.roundToInt())
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
    }

    private fun chain(field: Field, activity: Activity) {
        activity.runOnUiThread {
            _duringChain.value = true
        }
        Thread.sleep(chainSpeed)
        activity.runOnUiThread {
            _currentField.value = field.disappearingField
            _chainInfo.value = ChainInfo(field.bonus, field.disappearPuyo.size, field.chainPoint, field.accumulatedPoint, field.chainNum+1)
        }
        Thread.sleep(chainSpeed)
        activity.runOnUiThread {
            _currentField.value = field.nextField!!
        }
        if (field.nextField!!.nextField != null) {
            chain(field.nextField!!, activity)
        } else {
            activity.runOnUiThread {
                _duringChain.value = false
                normalChainSpeed()
            }
        }
    }

    fun searchBySeed(seed : Int, context: Context) : MutableList<Base> {
        if (seed !in 0..65535) {
            throw NumberFormatException("should enter 0-65535")
        }
        val bases = getFieldDB(context).baseDao().findByHash(seed)
        return bases.toMutableList()
    }

    fun searchByPattern(pattern : String, context: Context) : MutableList<Base> {
        val seeds = Haipuyo.searchSeedWithPattern(pattern)
        val seedsChunks = seeds.chunked(100)
        val bases = seedsChunks.parallelStream().map { seed: List<Int> -> getFieldDB(context).baseDao().findByAllHash(seed) }.flatMap { obj: List<Base> -> obj.stream() }.collect(
            Collectors.toList())
        return bases.toMutableList()
    }

    fun showAll (context: Context) : MutableList<Base> {
        val bases = getFieldDB(context).baseDao().all
        return bases.toMutableList()
    }

    fun checkAllClear() {
        viewModelScope.launch {
            _allClearLoading.value = true
            var result : AllClearInfo
            withContext(Dispatchers.Default) {
                result = searchAllClearFields(_currentField.value!!, tsumoController, 2)
            }
            _allClearInfo.value = result
            _allClearLoading.value = false
        }
    }

    companion object {
        private val RANDOM = Random()
    }

    init {
        try {
            val haipuyoIs = asset.open("haipuyo.txt")
            val haipuyoBr = BufferedReader(InputStreamReader(haipuyoIs))
            val sortedIs = asset.open("sorted_haipuyo.txt")
            val sortedBr = BufferedReader(InputStreamReader(sortedIs))
            Haipuyo.load(haipuyoBr, sortedBr)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val seed = RANDOM.nextInt(65536)
        _seed.value = seed
        tsumoController = TsumoController(Haipuyo[seed], seed)
        _tsumoInfo.value = tsumoController.makeTsumoInfo()
        clearFieldHistory()
        settingRepository  = SettingRepository(dataStore)
    }
}