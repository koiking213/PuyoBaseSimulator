package com.example.puyo_base_simulator.ui.home

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.gridlayout.widget.GridLayout
import com.example.puyo_base_simulator.R
import com.google.android.material.snackbar.Snackbar
import java.lang.NullPointerException
import java.util.*
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class HomeFragment : Fragment() {
    private lateinit var fieldView: Array<Array<ImageView>>
    private lateinit var currentPuyoView: Array<Array<ImageView>>
    private lateinit var nextPuyoView: Array<Array<ImageView>>
    private lateinit var currentPuyoLayout: GridLayout
    private lateinit var nextPuyoLayout: GridLayout
    private lateinit var mPresenter: HomePresenter
    private lateinit var mActivity: Activity
    private lateinit var mRoot: View
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
    private val specifiedSeed: Int
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

    @Composable
    fun CallToActionButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondary
            ),
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(20)
        ) {
            Text(text, fontSize = 10.sp)
        }
    }

    @Composable
    fun CursorKeys(size: Dp) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(5.dp)
        ) {
            Row(
                modifier = Modifier.padding(5.dp)
            ) {
                CallToActionButton(
                    text = "←",
                    onClick = {},
                    modifier = Modifier.size(size, size)
                );
                CallToActionButton(
                    text = "→",
                    onClick = {},
                    modifier = Modifier.size(size, size)
                );
            }
            CallToActionButton(
                text = "↓",
                onClick = {},
                modifier = Modifier.size(size, size)
            );
        }
    }

    @Composable
    fun RotationKeys(size: Dp) {
        Column(
            modifier = Modifier
                .width(size * 3 / 2)
                .padding(5.dp)
        ) {
            CallToActionButton(
                text = "A",
                onClick = {},
                modifier = Modifier
                    .size(size, size)
                    .absoluteOffset(size / 2, 0.dp)
            );
            CallToActionButton(
                text = "B",
                onClick = {},
                modifier = Modifier
                    .size(size, size)
                    .absoluteOffset(0.dp, 0.dp)
            );
        }
    }

    @Composable
    fun Cell(id: Int, size: Dp) {
        Image(
            painter = painterResource(id),
            contentDescription = null,
            modifier = Modifier.size(size, size)
        )
    }
    
    @Composable
    fun SaveLoad(size: Dp) {
        Row(
            modifier = Modifier.padding(5.dp)
        ) {
            CallToActionButton(
                text = "SAVE",
                onClick = {},
                modifier = Modifier
                    .height(size)
                    .width(size * 2)
            );
            CallToActionButton(
                text = "LOAD",
                onClick = {},
                modifier = Modifier
                    .height(size)
                    .width(size * 2)
            );
        }
    }

    @Composable
    fun TsumoControlButtonArea() {
        Row() {
            val size = 90.dp
            CursorKeys(size)
            RotationKeys(size)
        }
    }

    @Composable
    fun TextFieldWithButton(
        size: Dp,
        state: MutableState<TextFieldValue>,
        textLabel: String,
        buttonLabel: String
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = state.value,
                onValueChange = { state.value = it },
                label = { Text(textLabel, fontSize = 10.sp) },
                modifier = Modifier
                    .height(size)
                    .width(size * 2)
            )
            CallToActionButton(
                text = buttonLabel,
                onClick = {},
                modifier = Modifier
                    .height(size)
                    .width(size * 2)
            )
        }
    }

    // いい感じに大きさを決めたい
    @Composable
    fun FieldGeneration(size: Dp) {
        val seedTextState = remember { mutableStateOf(TextFieldValue())}
        val patternTextState = remember { mutableStateOf(TextFieldValue())}
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(5.dp),
        ) {
            TextFieldWithButton(size = size, state = seedTextState, textLabel = "seed", buttonLabel = "生成")
            TextFieldWithButton(size = size, state = patternTextState, textLabel = "pattern", buttonLabel = "生成")
            CallToActionButton(
                text = "ランダム生成",
                onClick = {},
                modifier = Modifier
                    .height(size)
                    .width(size * 3)
            );
        }
    }

    @Composable
    fun CurrentSeed(seed: Int) {
         Text("seed: $seed")
    }

    @Composable
    fun PuyoImage(color: PuyoColor, size: Dp) {
        val id = when(color) {
            PuyoColor.RED -> R.drawable.pr
            PuyoColor.BLUE -> R.drawable.pb
            PuyoColor.GREEN -> R.drawable.pg
            PuyoColor.YELLOW -> R.drawable.py
            PuyoColor.PURPLE -> R.drawable.pp
            PuyoColor.EMPTY -> R.drawable.blank
        }
        Cell(id, size)
    }

    @Composable
    fun Wall(size: Dp) {
        Cell(R.drawable.wall, size)
    }

    @Composable
    fun MainField(field: Field, size: Dp) {
        val colors = Array(13) { i -> Array(6) { j -> field.field[13-i-1][j].color} }
        PuyoField(puyoMatrix = colors, size)
    }

    @Composable
    fun PuyoField(puyoMatrix: Array<Array<PuyoColor>>, size: Dp) {
        Column() {
            for (r in puyoMatrix) {
                Row() {
                    for (c in r) {
                        PuyoImage(c, size)
                    }
                }
            }
        }
    }
    
    @Composable
    fun SideWall(size: Dp) {
        Column() {
            for (r in 0..13) {
                Wall(size)
            }
        }
    }

    @Composable
    fun BottomWall(size: Dp) {
        Row() {
            for (c in 0..5) {
                Wall(size)
            }
        }
    }

    private fun createNextTsumoColorMap(tsumoInfo: TsumoInfo) : Array<Array<PuyoColor>>{
        var ret = Array(4) {Array(2){PuyoColor.EMPTY}}
        ret[0][0] = tsumoInfo.nextColor[0][0]
        ret[1][0] = tsumoInfo.nextColor[0][1]
        ret[2][1] = tsumoInfo.nextColor[1][0]
        ret[3][1] = tsumoInfo.nextColor[1][1]
        return ret
    }

    private fun createCurrentTsumoColorMap(tsumoInfo: TsumoInfo) : Array<Array<PuyoColor>>{
        var ret = Array(3) {Array(6){PuyoColor.EMPTY}}
        val p1 = tsumoInfo.currentMainPos
        ret[p1.row][p1.column] = tsumoInfo.currentColor[0]
        val p2 = tsumoInfo.currentSubPos
        ret[p2.row][p2.column] = tsumoInfo.currentColor[1]
        return ret
    }

    @Composable
    fun CurrentTsumoFrame(tsumoInfo: TsumoInfo, size: Dp) {
        val colors = createCurrentTsumoColorMap(tsumoInfo)
        PuyoField(colors, size)
    }

    @Composable
    fun NextTsumoFrame(tsumoInfo: TsumoInfo, size: Dp) {
        val colors = createNextTsumoColorMap(tsumoInfo)
        PuyoField(colors, size)
    }
    
    @Composable
    fun FieldFrame(field: Field, tsumoInfo: TsumoInfo, size: Dp) {
        Row {
            Row(verticalAlignment = Alignment.Bottom)
            {
                SideWall(size)
                Column {
                    CurrentTsumoFrame(tsumoInfo, size)
                    MainField(field, size)
                    BottomWall(size)
                }
                SideWall(size)
            }
            NextTsumoFrame(tsumoInfo, size)
        }
    }

    @Composable
    fun SliderFrame() {
        var sliderPosition = remember { mutableStateOf(0f) }
        Slider(value = sliderPosition.value, onValueChange = { sliderPosition.value = it })
    }

    @Composable
    fun Home(field: Field, tsumoInfo: TsumoInfo)  {
        Column (
            modifier = Modifier.fillMaxHeight().padding(5.dp)
        )
        {
            Row(
                modifier = Modifier.weight(6f)
            ) {
                FieldFrame(field = field, tsumoInfo = tsumoInfo, 20.dp)
                Column (horizontalAlignment = Alignment.End){
                    CurrentSeed(seed = 0)
                    FieldGeneration(40.dp)
                    SaveLoad(40.dp)
                }
            }
            Box (
                modifier = Modifier.weight(1f)
            ) {
                SliderFrame()
            }
            Box (
                modifier = Modifier.weight(3f)
            ) {
                TsumoControlButtonArea()
            }
        }
    }

    @Preview
    @Composable
    fun PreviewGreeting() {
        //CallToAction button("SET SEED", mPresenter.set_seed(), )
        //MainField(field = Field())
        val sampleTsumoInfo = TsumoInfo(
            Array(2) {PuyoColor.RED},
            Array(2) {Array(2) {PuyoColor.RED}},
            3,
            Rotation.DEGREE0
        )
        val sampleField = Field.from("rrrbbbggg")
        //FieldFrame(field = sampleField, sampleTsumoInfo)
        Home(field = sampleField, sampleTsumoInfo)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        mRoot = root
        val activity = requireActivity()
        mActivity = activity

        return ComposeView(requireContext()).apply {
            setContent {
                Text("Hello world!")
            }
        }

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
        mPresenter = HomePresenter(requireActivity().assets)

        // ボタン群
        root.findViewById<View>(R.id.buttonUndo).setOnClickListener {
            mPresenter.undo()
            update()
            drawPoint(0, 0, 0, mPresenter.currentField.accumulatedPoint)
        }
        root.findViewById<View>(R.id.buttonRedo).setOnClickListener {
            drawEvaluatedField(mPresenter.redo())
            updateHistory()
        }
        root.findViewById<View>(R.id.buttonSave).setOnClickListener {
            if (mPresenter.save(mActivity as Context)) Snackbar.make(root, "saved", Snackbar.LENGTH_SHORT).show()
        }
        root.findViewById<View>(R.id.buttonLoad).setOnClickListener {
            val loadFieldPopup = LoadFieldPopup(mActivity)
            loadFieldPopup.height = WindowManager.LayoutParams.WRAP_CONTENT
            loadFieldPopup.isOutsideTouchable = true
            loadFieldPopup.isFocusable = true
            loadFieldPopup.showAsDropDown(mRoot.findViewById(R.id.textViewSeed))
            loadFieldPopup.setFieldSelectedListener { _: Int, fieldPreview: FieldPreview ->
                mPresenter.load(mActivity as Context, fieldPreview)
                loadFieldPopup.dismiss()
                initFieldPreference()
            }
        }
        root.findViewById<View>(R.id.buttonLeft).setOnClickListener { onTsumoControllButtonClick(mPresenter::moveLeft) }
        root.findViewById<View>(R.id.buttonRight).setOnClickListener { onTsumoControllButtonClick(mPresenter::moveRight) }
        root.findViewById<View>(R.id.buttonDown).setOnClickListener {
            drawEvaluatedField(mPresenter.dropDown())
            updateHistory()
        }
        root.findViewById<View>(R.id.buttonA).setOnClickListener {onTsumoControllButtonClick(mPresenter::rotateLeft) }
        root.findViewById<View>(R.id.buttonB).setOnClickListener { onTsumoControllButtonClick(mPresenter::rotateRight) }
        root.findViewById<View>(R.id.buttonSetSeed).setOnClickListener {
            try {
                mPresenter.setSeed(specifiedSeed)
                initFieldPreference()
            } catch (e: NumberFormatException) {  // ignore
            }
        }
        root.findViewById<View>(R.id.buttonGenerate).setOnClickListener {
            onTsumoControllButtonClick(mPresenter::generate)
            initFieldPreference()
        }
        root.findViewById<View>(R.id.buttonRestart).setOnClickListener { onTsumoControllButtonClick(mPresenter::restart) }
        val seekBar = root.findViewById<SeekBar>(R.id.seekBar)
        seekBar.max = 0
        seekBar.setOnSeekBarChangeListener(
                object: SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        if (p2) {
                            mPresenter.setHistoryIndex(p1)
                            clearPoint()
                            clearChainNum()
                            drawField(mPresenter.currentField)
                            drawTsumo(mPresenter.tsumoInfo, mPresenter.currentField)
                        }
                    }
                    override fun onStartTrackingTouch(p0: SeekBar?) {}
                    override fun onStopTrackingTouch(p0: SeekBar?) {
                        updateHistory()
                    }
                }
        )
        initFieldPreference()
        return root
    }

    private fun updateHistory() {
        val seekBar = mRoot.findViewById<SeekBar>(R.id.seekBar)
        val undoButton = mRoot.findViewById<Button>(R.id.buttonUndo)
        val redoButton = mRoot.findViewById<Button>(R.id.buttonRedo)
        undoButton.isEnabled = !mPresenter.mFieldHistory.isFirst()
        redoButton.isEnabled = !mPresenter.mFieldHistory.isLast()
        seekBar.max = mPresenter.mFieldHistory.size() - 1
        seekBar.progress = mPresenter.mFieldHistory.index
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

    private fun onTsumoControllButtonClick(func: () -> Unit) {
        func()
        update()
    }

    private fun initFieldPreference() {
        setSeedText(mPresenter.seed)
        clearPoint()
        clearChainNum()
        update()
    }

    private fun drawEvaluatedField(newField : Field?) {
        if (newField != null) {
            drawField(newField)
            if (newField.nextField == null) {
                drawTsumo(mPresenter.tsumoInfo, newField)
            } else {
                eraseCurrentPuyo()
                disableAllButtons()
                drawFieldChainRecursive(newField, true)
            }
        }
    }
    private fun setSeedText(seed: Int) {
        val view = mRoot.findViewById<TextView>(R.id.textViewSeed)
        view.text = getString(R.string.current_seed, seed)
        val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun drawField(field: Field) {
        field.field.flatten().map { (row, col, color) ->
            fieldView[row][col].setImageResource(getPuyoImage(color))
        }
    }

    // アニメーションにしたい
    private fun drawFieldChainRecursive(field: Field, disappear: Boolean) {
        Thread {
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
            if (disappear) {
                drawPoint(field.bonus, field.disappearPuyo.size, field.chainPoint, field.accumulatedPoint)
                drawChainNum(field.chainNum)
                mActivity.runOnUiThread { drawDisappearField(field) }
                drawFieldChainRecursive(field.nextField!!, false)
            } else {
                mActivity.runOnUiThread { drawField(field) }
                if (field.disappearPuyo.isEmpty()) {
                    // 終了処理
                    mActivity.runOnUiThread {
                        enableAllButtons()
                        val tsumoInfo = mPresenter.tsumoInfo
                        update(field, tsumoInfo)
                    }
                } else {
                    drawFieldChainRecursive(field, true)
                }
            }
        }.start()
    }

    private fun drawDisappearField(field: Field) {
        field.field.flatten().map { puyo ->
            val resource = if (field.isDisappear(puyo)) R.drawable.disappear else getPuyoImage(puyo.color)
            fieldView[puyo.row][puyo.column].setImageResource(resource)
        }
    }

    private fun drawPoint(bonus: Int, puyoNum: Int, chainSum: Int, gameSum: Int) {
        mActivity.runOnUiThread { (mRoot.findViewById<View>(R.id.chainInfoTextView) as TextView).text =
                getString(R.string.chain_info, bonus, puyoNum*10, bonus*puyoNum*10) }
        mActivity.runOnUiThread { (mRoot.findViewById<View>(R.id.chainPointTextView) as TextView).text =
                getString(R.string.chain_point, chainSum) }
        mActivity.runOnUiThread { (mRoot.findViewById<View>(R.id.gamePointTextView) as TextView).text =
                getString(R.string.game_point, gameSum) }
    }

    private fun drawChainNum(chainNum: Int) {
        mActivity.runOnUiThread { (mRoot.findViewById<View>(R.id.chainNumTextView) as TextView).text =
                getString(R.string.chain_num, chainNum) }
    }

    private fun clearPoint() {
        (mRoot.findViewById<View>(R.id.chainInfoTextView) as TextView).text = ""
        (mRoot.findViewById<View>(R.id.chainPointTextView) as TextView).text = ""
        (mRoot.findViewById<View>(R.id.gamePointTextView) as TextView).text = ""
    }

    private fun clearChainNum() {
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

    private fun update(field: Field = mPresenter.currentField, tsumoInfo: TsumoInfo = mPresenter.tsumoInfo) {
        updateHistory()
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

    fun drawTsumo(tsumoInfo: TsumoInfo, field: Field) {
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

    private fun disableAllButtons() {
        buttons.map {mRoot.findViewById<View>(it).isEnabled = false}
    }

    private fun enableAllButtons() {
        buttons.map {mRoot.findViewById<View>(it).isEnabled = true}
        mRoot.findViewById<View>(R.id.buttonUndo).isEnabled = !mPresenter.mFieldHistory.isFirst()
        mRoot.findViewById<View>(R.id.buttonRedo).isEnabled = !mPresenter.mFieldHistory.isLast()
    }

    private fun eraseCurrentPuyo() {
        currentPuyoView.flatten().map { it.setImageResource(R.drawable.blank) }
    }
}