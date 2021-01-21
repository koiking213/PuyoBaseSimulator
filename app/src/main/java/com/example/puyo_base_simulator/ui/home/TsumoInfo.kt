package com.example.puyo_base_simulator.ui.home


class TsumoInfo internal constructor(currentColor: Array<PuyoColor>, nextColor: Array<Array<PuyoColor>>, index: Int, rot: Rotation) {
    @JvmField
    internal var currentColor: Array<PuyoColor>
    @JvmField
    internal var nextColor: Array<Array<PuyoColor>>
    @JvmField
    var currentMainPos = intArrayOf(1, 3) // row, column
    @JvmField
    var currentSubPos = intArrayOf(0, 3)
    @JvmField
    var currentCursorRotate: Rotation

    init {
        this.currentColor = currentColor.clone()
        this.nextColor = nextColor.clone()
        currentMainPos[0] = 1
        currentMainPos[1] = index
        when (rot) {
            Rotation.DEGREE0 -> {
                currentSubPos[0] = 0
                currentSubPos[1] = index
            }
            Rotation.DEGREE90 -> {
                currentSubPos[0] = 1
                currentSubPos[1] = index + 1
            }
            Rotation.DEGREE180 -> {
                currentSubPos[0] = 2
                currentSubPos[1] = index
            }
            Rotation.DEGREE270 -> {
                currentSubPos[0] = 1
                currentSubPos[1] = index - 1
            }
        }
        currentCursorRotate = rot
    }
}