package com.example.puyo_base_simulator.ui.home

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.gridlayout.widget.GridLayout
import com.example.puyo_base_simulator.R
import com.google.android.material.snackbar.Snackbar
import java.util.*

class HomeFragment : Fragment(), HomeContract.View {
    private lateinit var fieldView: Array<Array<ImageView>>
    private lateinit var currentPuyoView: Array<Array<ImageView>>
    private lateinit var nextPuyoView: Array<Array<ImageView>>
    private lateinit var currentPuyoLayout: GridLayout
    private lateinit var nextPuyoLayout: GridLayout
    private lateinit var mPresenter: HomePresenter
    private lateinit var mActivity: Activity
    private lateinit var mRoot: View
    private lateinit var mFieldHistory : SeekableHistory<Field>
    private val buttons = arrayOf(
            R.id.buttonLeft,
            R.id.buttonRight,
            R.id.buttonDown,
            R.id.buttonA,
            R.id.buttonB,
            R.id.buttonUndo,
            R.id.buttonRedo,
            R.id.buttonLoad,
            R.id.buttonSave,
    )
    override val specifiedSeed: Int
        get() {
            val editText = mRoot.findViewById<EditText>(R.id.editTextSeed)
            return try {
                val seed = editText.text.toString().toInt()
                if (seed in 0..65535) {
                    seed
                } else {
                    throw NumberFormatException()
                }
            } catch (e: NumberFormatException) {
                editText.error = "should enter 0-65535."
                throw e
            }

        }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        mRoot = root
        val activity = requireActivity()
        mActivity = activity

        // current puyo area
        currentPuyoView = Array(3) { i -> Array(7) { j ->
            val view = ImageView(getActivity())
            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(i)
            params.columnSpec = GridLayout.spec(j)
            view.layoutParams = params
            view.setImageResource(R.drawable.blank)
            view
        } }
        currentPuyoLayout = root.findViewById(R.id.currentPuyoLayout)
        currentPuyoView.flatten().map {currentPuyoLayout.addView(it)}

        // next puyo area
        val views = Array(4) { i -> Array(2) { j ->
            val view = ImageView(getActivity())
            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(i)
            params.columnSpec = GridLayout.spec(j)
            view.layoutParams = params
            view.setImageResource(R.drawable.blank)
            view
        } }
        nextPuyoView = arrayOf(arrayOf(views[0][0], views[1][0]), arrayOf(views[2][1], views[3][1]))
        nextPuyoLayout = root.findViewById(R.id.nextPuyoLayout)
        views.flatten().map {nextPuyoLayout.addView(it)}

        // main field
        val fieldLayout: GridLayout = root.findViewById(R.id.fieldLayout)
        fieldView = Array(14) { i -> Array (8) { j ->
            val view = ImageView(getActivity())
            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(13-i)
            params.columnSpec = GridLayout.spec(j)
            view.layoutParams = params
            fieldLayout.addView(view)
            view
        }}

        // wall
        for (i in 0..12) {
            fieldView[i][0].setImageResource(R.drawable.wall)
            fieldView[i][7].setImageResource(R.drawable.wall)
        }
        for (j in 1..6) {
            fieldView[0][j].setImageResource(R.drawable.wall)
        }
        mPresenter = HomePresenter(this, requireActivity().assets, mActivity)

        // ボタン群
        root.findViewById<View>(R.id.buttonUndo).setOnClickListener { mPresenter.undo() }
        root.findViewById<View>(R.id.buttonRedo).setOnClickListener { mPresenter.redo() }
        root.findViewById<View>(R.id.buttonSave).setOnClickListener {
            mPresenter.save()
            Snackbar.make(root, "saved", Snackbar.LENGTH_SHORT).show()
        }
        root.findViewById<View>(R.id.buttonLoad).setOnClickListener {
            val loadFieldPopup = LoadFieldPopup(mActivity)
            loadFieldPopup.height = WindowManager.LayoutParams.WRAP_CONTENT
            loadFieldPopup.isOutsideTouchable = true
            loadFieldPopup.isFocusable = true
            loadFieldPopup.showAsDropDown(mRoot.findViewById(R.id.textViewSeed))
            loadFieldPopup.setFieldSelectedListener { _: Int, fieldPreview: FieldPreview ->
                mPresenter.load(fieldPreview)
                loadFieldPopup.dismiss()
            }
        }
        root.findViewById<View>(R.id.buttonLeft).setOnClickListener { mPresenter.moveLeft() }
        root.findViewById<View>(R.id.buttonRight).setOnClickListener { mPresenter.moveRight() }
        root.findViewById<View>(R.id.buttonDown).setOnClickListener { mPresenter.dropDown() }
        root.findViewById<View>(R.id.buttonA).setOnClickListener { mPresenter.rotateLeft() }
        root.findViewById<View>(R.id.buttonB).setOnClickListener { mPresenter.rotateRight() }
        root.findViewById<View>(R.id.buttonSetSeed).setOnClickListener { mPresenter.setSeed() }
        root.findViewById<View>(R.id.buttonGenerate).setOnClickListener { mPresenter.generate() }
        root.findViewById<View>(R.id.buttonRestart).setOnClickListener { mPresenter.restart() }
        val seekBar = root.findViewById<SeekBar>(R.id.seekBar)
        seekBar.max = 0
        seekBar.setOnSeekBarChangeListener(
                object: SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                    }
                }
        )
        mFieldHistory = SeekableHistory(seekBar,
                root.findViewById(R.id.buttonUndo),
                root.findViewById(R.id.buttonRedo)
        )
        mFieldHistory.add(Field())
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener { v, event ->
            v.performClick()
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                view.requestFocus()
            }
            v?.onTouchEvent(event) ?: true
        }
        val editText = mRoot.findViewById<EditText>(R.id.editTextSeed)
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

    override fun setSeedText(seed: Int) {
        val view = mRoot.findViewById<TextView>(R.id.textViewSeed)
        view.text = String.format(Locale.JAPAN, "seed: %d", seed)
        val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun drawField(field: Field) {
        field.field.flatten().map { (row, col, color) ->
            fieldView[row][col].setImageResource(getPuyoImage(color))
        }
    }

    override fun drawDisappearField(field: Field) {
        field.field.flatten().map { puyo ->
            val resource = if (field.isDisappear(puyo)) R.drawable.disappear else getPuyoImage(puyo.color)
            fieldView[puyo.row][puyo.column].setImageResource(resource)
        }
    }

    override fun drawPoint(bonus: Int, puyoNum: Int, chainSum: Int, gameSum: Int) {
        mActivity.runOnUiThread { (mRoot.findViewById<View>(R.id.chainInfoTextView) as TextView).text =
                getString(R.string.chain_info, bonus, puyoNum*10, bonus*puyoNum*10) }
        mActivity.runOnUiThread { (mRoot.findViewById<View>(R.id.chainPointTextView) as TextView).text =
                getString(R.string.chain_point, chainSum) }
        mActivity.runOnUiThread { (mRoot.findViewById<View>(R.id.gamePointTextView) as TextView).text =
                getString(R.string.game_point, gameSum) }
    }

    override fun drawChainNum(chainNum: Int) {
        mActivity.runOnUiThread { (mRoot.findViewById<View>(R.id.chainNumTextView) as TextView).text =
                getString(R.string.chain_num, chainNum) }
    }

    override fun clearPoint() {
        (mRoot.findViewById<View>(R.id.chainInfoTextView) as TextView).text = ""
        (mRoot.findViewById<View>(R.id.chainPointTextView) as TextView).text = ""
        (mRoot.findViewById<View>(R.id.gamePointTextView) as TextView).text = ""
    }

    override fun clearChainNum() {
        (mRoot.findViewById<View>(R.id.chainNumTextView) as TextView).text = ""
    }

private fun getPuyoImage(color: PuyoColor): Int {
        return when (color) {
            PuyoColor.RED -> R.drawable.pr
            PuyoColor.BLUE -> R.drawable.pb
            PuyoColor.YELLOW -> R.drawable.py
            PuyoColor.GREEN -> R.drawable.pg
            PuyoColor.PURPLE -> R.drawable.pp
            PuyoColor.EMPTY -> R.drawable.blank
        }
    }

    private fun getDotImage(color: PuyoColor): Int {
        return when (color) {
            PuyoColor.RED -> R.drawable.dotr
            PuyoColor.BLUE -> R.drawable.dotb
            PuyoColor.YELLOW -> R.drawable.doty
            PuyoColor.GREEN -> R.drawable.dotg
            PuyoColor.PURPLE -> R.drawable.dotp
            PuyoColor.EMPTY -> R.drawable.blank
        }
    }

    override fun update(field: Field, tsumoInfo: TsumoInfo) {
        drawField(field)
        drawTsumo(tsumoInfo, field)
    }

    // リストで渡された順に下から積み上げる
    private fun drawDot(column: Int, colors: List<PuyoColor>, field: Field) {
        var row = field.getHeight(column) + 1
        for (color in colors) {
            if (row <= 13) {
                fieldView[row++][column].setImageResource(getDotImage(color))
            }
        }
    }

    override fun drawTsumo(tsumoInfo: TsumoInfo, field: Field) {
        // draw current
        currentPuyoView.flatten().map { it.setImageResource(R.drawable.blank) }
        val mainColor = tsumoInfo.currentColor[0]
        val subColor = tsumoInfo.currentColor[1]
        currentPuyoView[tsumoInfo.currentMainPos.row][tsumoInfo.currentMainPos.column].setImageResource(getPuyoImage(mainColor))
        currentPuyoView[tsumoInfo.currentSubPos.row][tsumoInfo.currentSubPos.column].setImageResource(getPuyoImage(subColor))

        // draw next and next next
        for (i in 0..1) {
            for (j in 0..1) {
                nextPuyoView[i][j].setImageResource(getPuyoImage(tsumoInfo.nextColor[i][j]))
            }
        }

        // draw dot
        when (tsumoInfo.rot) {
            Rotation.DEGREE0 -> drawDot(tsumoInfo.currentMainPos.column, listOf(mainColor, subColor), field)
            Rotation.DEGREE90, Rotation.DEGREE270 -> {
                drawDot(tsumoInfo.currentMainPos.column, listOf(mainColor), field)
                drawDot(tsumoInfo.currentSubPos.column, listOf(subColor), field)
            }
            Rotation.DEGREE180 -> drawDot(tsumoInfo.currentMainPos.column, listOf(subColor, mainColor), field)
        }
    }

    override fun disableUndoButton() {
        mRoot.findViewById<View>(R.id.buttonUndo).isEnabled = false
    }

    override fun enableUndoButton() {
        mRoot.findViewById<View>(R.id.buttonUndo).isEnabled = true
    }

    override fun disableRedoButton() {
        mRoot.findViewById<View>(R.id.buttonRedo).isEnabled = false
    }

    override fun enableRedoButton() {
        mRoot.findViewById<View>(R.id.buttonRedo).isEnabled = true
    }

    override fun disableAllButtons() {
        buttons.map {mRoot.findViewById<View>(it).isEnabled = false}
    }

    override fun enableAllButtons() {
        buttons.map {mRoot.findViewById<View>(it).isEnabled = true}
    }

    override fun eraseCurrentPuyo() {
        currentPuyoView.flatten().map { it.setImageResource(R.drawable.blank) }
    }

    // TODO: mFieldHistoryをpublicにしたほうがいいのでは？
    override fun clearHistory() {
        mFieldHistory.clear()
        mFieldHistory.add(Field())
    }

    override fun appendHistory(f: Field) {
        mFieldHistory.add(f)
    }

    override fun undoHistory() : Field {
        return mFieldHistory.undo()!!
    }

    override fun undoAllHistory() : Field {
        return mFieldHistory.undoAll()
    }

    override fun redoHistory() : Field {
        return mFieldHistory.redo()!!
    }

    override fun latestHistory() : Field {
        return mFieldHistory.latest()
    }
}