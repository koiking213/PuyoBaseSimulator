package com.example.puyo_base_simulator.ui.home


data class TsumoInfo(val currentColor: Array<PuyoColor>,
                     val nextColor: Array<Array<PuyoColor>>,
                     private var column: Int,
                     val rot: Rotation) {
    val currentMainPos = Point(1, column) // row, column
    val currentSubPos = when (rot) {
        Rotation.DEGREE0 -> Point(0, column)
        Rotation.DEGREE90 -> Point(1, column+1)
        Rotation.DEGREE180 -> Point(2, column)
        Rotation.DEGREE270 -> Point(1, column-1)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TsumoInfo

        if (!currentColor.contentEquals(other.currentColor)) return false
        if (!nextColor.contentDeepEquals(other.nextColor)) return false
        if (column != other.column) return false
        if (rot != other.rot) return false
        if (currentMainPos != other.currentMainPos) return false
        if (currentSubPos != other.currentSubPos) return false

        return true
    }

    override fun hashCode(): Int {
        var result = currentColor.contentHashCode()
        result = 31 * result + nextColor.contentDeepHashCode()
        result = 31 * result + column
        result = 31 * result + rot.hashCode()
        result = 31 * result + currentMainPos.hashCode()
        result = 31 * result + currentSubPos.hashCode()
        return result
    }
}