package com.example.puyo_base_simulator.ui.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.gridlayout.widget.GridLayout
import com.example.puyo_base_simulator.R
import com.example.puyo_base_simulator.ui.home.PuyoColor
import java.util.*

class HomeFragment : Fragment(), HomeContract.View {
    lateinit var fieldView: Array<Array<ImageView?>>
    lateinit var currentPuyoView: Array<Array<ImageView?>>
    lateinit var nextPuyoView: Array<Array<ImageView?>>
    var currentPuyoLayout: GridLayout? = null
    var nextPuyoLayout: GridLayout? = null
    private var mPresenter: HomePresenter? = null
    private lateinit var mActivity: Activity
    private var mRoot: View? = null
    override val specifiedSeed: Int
        get() {
            val editText = mRoot!!.findViewById<EditText>(R.id.editTextSeed)
            return try {
                val seed = editText.text.toString().toInt()
                if (0 <= seed && seed <= 65535) {
                    seed
                } else {
                    throw NumberFormatException()
                }
            } catch (e: NumberFormatException) {
                editText.error = "should enter 0-65535."
                throw e
            }

        }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        mRoot = root
        val activity: Activity = requireActivity()
        mActivity = activity

        // current puyo area
        currentPuyoView = Array(3) { arrayOfNulls(7) }
        currentPuyoLayout = root.findViewById(R.id.currentPuyoLayout)
        for (i in 0..2) {
            for (j in 0..6) {
                val view = ImageView(getActivity())
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(i)
                params.columnSpec = GridLayout.spec(j)
                view.layoutParams = params
                view.setImageResource(R.drawable.blank)
                currentPuyoLayout?.addView(view)
                currentPuyoView[i][j] = view
            }
        }

        // next puyo area
        nextPuyoView = Array(4) { arrayOfNulls(2) }
        nextPuyoLayout = root.findViewById(R.id.nextPuyoLayout)
        val views = Array(4) { arrayOfNulls<ImageView>(2) }
        for (i in 0..3) {
            for (j in 0..1) {
                val view = ImageView(getActivity())
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(i)
                params.columnSpec = GridLayout.spec(j)
                view.layoutParams = params
                view.setImageResource(R.drawable.blank)
                nextPuyoLayout?.addView(view)
                views[i][j] = view
            }
        }
        nextPuyoView[0][0] = views[0][0]
        nextPuyoView[0][1] = views[1][0]
        nextPuyoView[1][0] = views[2][1]
        nextPuyoView[1][1] = views[3][1]

        // main field
        val fieldLayout: GridLayout = root.findViewById(R.id.fieldLayout)
        fieldView = Array(14) { arrayOfNulls(8) }
        for (i in 0..13) {
            for (j in 0..7) {
                val view = ImageView(getActivity())
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(i)
                params.columnSpec = GridLayout.spec(j)
                view.layoutParams = params
                fieldLayout.addView(view)
                fieldView[13 - i][j] = view
            }
        }

        // wall
        for (i in 0..13) {
            fieldView[i][0]!!.setImageResource(R.drawable.wall)
            fieldView[i][7]!!.setImageResource(R.drawable.wall)
        }
        for (j in 1..6) {
            fieldView[0][j]!!.setImageResource(R.drawable.wall)
        }
        (root.findViewById<View>(R.id.pointTextView) as TextView).text = "0点"
        mPresenter = HomePresenter(this, requireActivity().assets, mActivity!!)

        // ボタン群
        root.findViewById<View>(R.id.buttonUndo).setOnClickListener { v: View? -> mPresenter!!.undo() }
        root.findViewById<View>(R.id.buttonRedo).setOnClickListener { v: View? -> mPresenter!!.redo() }
        root.findViewById<View>(R.id.buttonSave).setOnClickListener { v: View? -> mPresenter!!.save() }
        root.findViewById<View>(R.id.buttonLoad).setOnClickListener { v: View? ->
            val loadFieldPopup = LoadFieldPopup(mActivity)
            loadFieldPopup.height = WindowManager.LayoutParams.WRAP_CONTENT
            loadFieldPopup.isOutsideTouchable = true
            loadFieldPopup.isFocusable = true
            loadFieldPopup.showAsDropDown(mRoot!!.findViewById(R.id.buttonLoad))
            loadFieldPopup.setFieldSelectedListener { position: Int, fieldPreview: FieldPreview ->
                mPresenter!!.load(fieldPreview)
                loadFieldPopup.dismiss()
            }
        }
        root.findViewById<View>(R.id.buttonLeft).setOnClickListener { v: View? -> mPresenter!!.moveLeft() }
        root.findViewById<View>(R.id.buttonRight).setOnClickListener { v: View? -> mPresenter!!.moveRight() }
        root.findViewById<View>(R.id.buttonDown).setOnClickListener { v: View? -> mPresenter!!.dropDown() }
        root.findViewById<View>(R.id.buttonA).setOnClickListener { v: View? -> mPresenter!!.rotateLeft() }
        root.findViewById<View>(R.id.buttonB).setOnClickListener { v: View? -> mPresenter!!.rotateRight() }
        root.findViewById<View>(R.id.buttonSetSeed).setOnClickListener { v: View? -> mPresenter!!.setSeed() }
        return root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun setSeedText(seed: Int) {
        val view = mRoot!!.findViewById<TextView>(R.id.textViewSeed)
        view.text = String.format(Locale.JAPAN, "seed: %d", seed)
    }

    override fun drawField(field: Field) {
        for (i in 1..13) {
            for (j in 1..6) {
                val (_, _, color) = field.getFieldContent(i,j)!!
                fieldView[i][j]!!.setImageResource(getPuyoImage(color))
            }
        }
    }

    override fun drawDisappearField(field: Field) {
        for (i in 1..13) {
            for (j in 1..6) {
                val puyo = field.getFieldContent(i,j)!!
                if (field.isDisappear(puyo)) {
                    fieldView[i][j]!!.setImageResource(R.drawable.disappear)
                } else {
                    fieldView[i][j]!!.setImageResource(getPuyoImage(puyo.color))
                }
            }
        }
    }

    override fun drawPoint(text: String) {
        mActivity!!.runOnUiThread { (mRoot!!.findViewById<View>(R.id.pointTextView) as TextView).text = text }
    }

    fun getPuyoImage(color: PuyoColor?): Int {
        return when (color) {
            PuyoColor.RED -> R.drawable.pr
            PuyoColor.BLUE -> R.drawable.pb
            PuyoColor.YELLOW -> R.drawable.py
            PuyoColor.GREEN -> R.drawable.pg
            PuyoColor.PURPLE -> R.drawable.pp
            PuyoColor.EMPTY -> R.drawable.blank
            else -> -1
        }
    }

    fun getDotImage(color: PuyoColor?): Int {
        return when (color) {
            PuyoColor.RED -> R.drawable.dotr
            PuyoColor.BLUE -> R.drawable.dotb
            PuyoColor.YELLOW -> R.drawable.doty
            PuyoColor.GREEN -> R.drawable.dotg
            PuyoColor.PURPLE -> R.drawable.dotp
            else -> -1
        }
    }

    override fun update(field: Field, tsumoInfo: TsumoInfo) {
        drawField(field)
        drawTsumo(tsumoInfo, field)
    }

    // リストで渡された順に下から積み上げる
    fun drawDot(column: Int, colors: List<PuyoColor?>, field: Field) {
        var row = field.getHeight(column) + 1
        for (color in colors) {
            if (row <= 13) {
                fieldView[row++][column]!!.setImageResource(getDotImage(color))
            }
        }
    }

    override fun drawTsumo(tsumoInfo: TsumoInfo, field: Field) {
        // draw current
        for (i in 0..2) {
            for (j in 0..6) {
                currentPuyoView[i][j]!!.setImageResource(R.drawable.blank)
            }
        }
        val jikuColor = getPuyoImage(tsumoInfo.currentColor[0])
        val nonJikuColor = getPuyoImage(tsumoInfo.currentColor[1])
        // draw jiku-puyo
        currentPuyoView[tsumoInfo.currentMainPos[0]][tsumoInfo.currentMainPos[1]]!!.setImageResource(jikuColor)

        // draw not-jiku-puyo
        currentPuyoView[tsumoInfo.currentSubPos[0]][tsumoInfo.currentSubPos[1]]!!.setImageResource(nonJikuColor)

        // draw next and next next
        for (i in 0..1) {
            for (j in 0..1) {
                nextPuyoView[i][j]!!.setImageResource(getPuyoImage(tsumoInfo.nextColor[i][j]))
            }
        }

        // draw dot
        val currentColor = arrayOfNulls<PuyoColor>(2)
        currentColor[0] = tsumoInfo.currentColor[0]
        currentColor[1] = tsumoInfo.currentColor[1]
        val currentCursorRotate = tsumoInfo.currentCursorRotate
        when (currentCursorRotate) {
            Rotation.DEGREE0 -> drawDot(tsumoInfo.currentMainPos[1], Arrays.asList(currentColor[0], currentColor[1]), field)
            Rotation.DEGREE90, Rotation.DEGREE270 -> {
                drawDot(tsumoInfo.currentMainPos[1], listOf(currentColor[0]), field)
                drawDot(tsumoInfo.currentSubPos[1], listOf(currentColor[1]), field)
            }
            Rotation.DEGREE180 -> drawDot(tsumoInfo.currentMainPos[1], Arrays.asList(currentColor[1], currentColor[0]), field)
        }
    }

    override fun disableUndoButton() {
        mRoot!!.findViewById<View>(R.id.buttonUndo).isEnabled = false
    }

    override fun enableUndoButton() {
        mRoot!!.findViewById<View>(R.id.buttonUndo).isEnabled = true
    }

    override fun disableRedoButton() {
        mRoot!!.findViewById<View>(R.id.buttonRedo).isEnabled = false
    }

    override fun enableRedoButton() {
        mRoot!!.findViewById<View>(R.id.buttonRedo).isEnabled = true
    }

    override fun disableAllButtons() {
        mRoot!!.findViewById<View>(R.id.buttonLeft).isEnabled = false
        mRoot!!.findViewById<View>(R.id.buttonRight).isEnabled = false
        mRoot!!.findViewById<View>(R.id.buttonDown).isEnabled = false
        mRoot!!.findViewById<View>(R.id.buttonA).isEnabled = false
        mRoot!!.findViewById<View>(R.id.buttonB).isEnabled = false
        mRoot!!.findViewById<View>(R.id.buttonUndo).isEnabled = false
        mRoot!!.findViewById<View>(R.id.buttonRedo).isEnabled = false
    }

    override fun enableAllButtons() {
        mRoot!!.findViewById<View>(R.id.buttonLeft).isEnabled = true
        mRoot!!.findViewById<View>(R.id.buttonRight).isEnabled = true
        mRoot!!.findViewById<View>(R.id.buttonDown).isEnabled = true
        mRoot!!.findViewById<View>(R.id.buttonA).isEnabled = true
        mRoot!!.findViewById<View>(R.id.buttonB).isEnabled = true
        mRoot!!.findViewById<View>(R.id.buttonUndo).isEnabled = true
        mRoot!!.findViewById<View>(R.id.buttonRedo).isEnabled = true
    }

    override fun eraseCurrentPuyo() {
        for (i in 0..2) {
            for (j in 0..6) {
                currentPuyoView[i][j]!!.setImageResource(R.drawable.blank)
            }
        }
    }
}