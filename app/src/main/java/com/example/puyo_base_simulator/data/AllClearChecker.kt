package com.example.puyo_base_simulator.data

import com.example.puyo_base_simulator.utils.Rotation
import org.apache.commons.lang3.SerializationUtils

// 幅優先探索
fun searchAllClearFields(field: Field, tsumoController: TsumoController, depth: Int) : AllClearInfo {
    val allClearInfo = AllClearInfo()
    val controller = SerializationUtils.clone(tsumoController) as TsumoController
    var fields = listOf(field)

    for (i in 0..depth) {
        val tsumoInfo = controller.makeTsumoInfo()
        val newFields = fields.flatMap { generateAllFields(it, tsumoInfo) }.distinctBy { it.toString()}
        val allClearFields = getAllClearFields(newFields)
        if (allClearFields.isNotEmpty()) allClearInfo.add(i, allClearFields)
        controller.incrementTsumo()
        fields = newFields.map {it.getLast()}.distinctBy { it.toString() }
    }
    return allClearInfo
}


fun getAllClearFields(fields: List<Field>) : List<Field> {
    val ret = mutableListOf<Field>()
    for (f in fields) {
        if (f.getLast().allClear()) ret.add(f)
    }
    return ret
}

fun generateAllTsumoPattern(tsumoInfo: TsumoInfo) : List<TsumoInfo> {
    val ret = mutableListOf<TsumoInfo>()

    val currentColor = tsumoInfo.currentColor
    val nextColor = tsumoInfo.nextColor
    for (col in 1..6) {
        for (rot in Rotation.values()) {
            if (col == 1 && rot == Rotation.DEGREE270) continue
            if (col == 6 && rot == Rotation.DEGREE90) continue
            ret.add(TsumoInfo(currentColor, nextColor, col, rot))
        }
    }
    return ret
}

fun generateAllFields(field: Field, tsumoInfo: TsumoInfo) : List<Field> {
    val ret = mutableListOf<Field>()
    val allTsumo = generateAllTsumoPattern(tsumoInfo)
    for (tsumo in allTsumo) {
        val newField = field.setPairOnField(tsumo) ?: continue
        newField.evalNextField()
        ret.add(newField)
    }
    return ret
}