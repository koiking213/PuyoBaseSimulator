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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.isLiveLiteralsEnabled
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.puyo_base_simulator.data.Base
import kotlinx.coroutines.launch
import kotlin.math.*

@ExperimentalComposeUiApi
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
    fun ChainInfoArea(field: Field) {
        val puyoNum = field.disappearPuyo.size
        Text("${field.bonus} * ${puyoNum*10} = ${field.bonus*puyoNum*10}")
        Text("連鎖の合計: ${field.chainPoint}")
        Text("試合の合計: ${field.accumulatedPoint}")
        Text("${field.chainNum}連鎖")
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
    fun CursorKeys(
        onLeftClick: () -> Unit,
        onRightClick: () -> Unit,
        onDownClick: () -> Unit,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(5.dp)
        ) {
            Row(
                modifier = Modifier.padding(5.dp)
            ) {
                CallToActionButton(
                    text = "←",
                    onClick = onLeftClick,
                    modifier = Modifier.size(90.dp)
                );
                CallToActionButton(
                    text = "→",
                    onClick = onRightClick,
                    modifier = Modifier.size(90.dp)
                );
            }
            CallToActionButton(
                text = "↓",
                onClick = onDownClick,
                modifier = Modifier.size(90.dp)
            );
        }
    }

    @Composable
    fun RotationKeys(
        onAClick: () -> Unit,
        onBClick: () -> Unit,
    ) {
        Column(
            modifier = Modifier.padding(5.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .width(180.dp)
                    .padding(5.dp)
            ) {
                CallToActionButton(
                    text = "A",
                    onClick = onAClick,
                    modifier = Modifier.size(90.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.width(180.dp)
            ) {
                CallToActionButton(
                    text = "B",
                    onClick = onBClick,
                    modifier = Modifier.size(90.dp)
                )
            }
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
    fun SaveLoad(
        onSaveClick: () -> Unit,
        onLoadClick: () -> Unit,
    ) {
        Row(
            modifier = Modifier.padding(5.dp)
        ) {
            CallToActionButton(
                text = "SAVE",
                onClick = onSaveClick,
            );
            CallToActionButton(
                text = "LOAD",
                onClick = onLoadClick,
            );
        }
    }

    @Composable
    fun TsumoControlButtonArea(
        onAClick: () -> Unit,
        onBClick: () -> Unit,
        onLeftClick: () -> Unit,
        onRightClick: () -> Unit,
        onDownClick: () -> Unit,
    ) {
        Row() {
            CursorKeys(onLeftClick, onRightClick, onDownClick)
            RotationKeys(onAClick, onBClick)
        }
    }

    @Composable
    fun TextFieldWithButton(
        size: Dp,
        isError: Boolean,
        textLabel: String,
        keyboardType: KeyboardType,
        onClick: (TextFieldValue) -> Unit
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current

        val state = remember { mutableStateOf(TextFieldValue()) }
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                //isError = isError,
                value = state.value,
                onValueChange = { state.value = it },
                label = { Text(textLabel, fontSize = 10.sp) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onClick(state.value)
                        keyboardController?.hide()
                    }
                ),
                singleLine = true,
                modifier = Modifier.size((10*textLabel.length).dp, size)
            )
        }
    }

    // いい感じに大きさを決めたい
    @Composable
    fun FieldGeneration(
        size: Dp,
        onSeedGenClicked: (TextFieldValue) -> Unit,
        onPatternGenClicked: (TextFieldValue) -> Unit,
        onRandomGenClicked: () -> Unit,
    ) {
        val (seedError, setSeedError) = remember { mutableStateOf(false)}
        val (patternError, setPatternError) = remember { mutableStateOf(false)}
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(5.dp),
        ) {
            TextFieldWithButton(
                size = size,
                isError = seedError,
                textLabel = "generate by seed",
                keyboardType = KeyboardType.Number,
                onClick = onSeedGenClicked
            )
            TextFieldWithButton(
                size = size,
                isError = patternError,
                textLabel = "generate by pattern",
                keyboardType = KeyboardType.Ascii,
                onClick = onPatternGenClicked
            )
            CallToActionButton(
                text = "ランダム生成",
                onClick = onRandomGenClicked,
                modifier = Modifier
                    .height(size)
                    .width(size * 3)
            )
        }
    }

    @Composable
    fun CurrentSeed(seed: Int) {
         Text("seed: $seed")
    }

    private fun puyoResourceId(color: PuyoColor) : Int {
        return when(color) {
            PuyoColor.RED -> R.drawable.pr
            PuyoColor.BLUE -> R.drawable.pb
            PuyoColor.GREEN -> R.drawable.pg
            PuyoColor.YELLOW -> R.drawable.py
            PuyoColor.PURPLE -> R.drawable.pp
            PuyoColor.EMPTY -> R.drawable.blank
            PuyoColor.DISAPPEAR -> R.drawable.disappear
        }
    }

    @Composable
    fun Wall(size: Dp) {
        Cell(R.drawable.wall, size)
    }

    @Composable
    fun MainField(field: Field, tsumoInfo: TsumoInfo, size: Dp) {
        val colors = Array(13) { i -> Array(6) { j -> puyoResourceId(field.field[i][j].color)} }
        val colorsWithDot = dotColors(tsumoInfo = tsumoInfo, field = field, colors)
        PuyoField(colorsWithDot.reversed().toTypedArray(), size)
    }

    @Composable
    fun PuyoField(colors: Array<Array<Int>>, size: Dp) {
        Column() {
            for (row in colors) {
                Row() {
                    for (c in row) {
                        Cell(c, size)
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

    private fun createNextTsumoColorMap(tsumoInfo: TsumoInfo) : Array<Array<Int>>{
        var ret = Array(4) {Array(2){puyoResourceId(PuyoColor.EMPTY)}}
        ret[0][0] = puyoResourceId(tsumoInfo.nextColor[0][0])
        ret[1][0] = puyoResourceId(tsumoInfo.nextColor[0][1])
        ret[2][1] = puyoResourceId(tsumoInfo.nextColor[1][0])
        ret[3][1] = puyoResourceId(tsumoInfo.nextColor[1][1])
        return ret
    }

    private fun createCurrentTsumoColorMap(tsumoInfo: TsumoInfo) : Array<Array<Int>>{
        var ret = Array(3) {Array(6){puyoResourceId(PuyoColor.EMPTY)}}
        val p1 = tsumoInfo.currentMainPos
        ret[p1.row][p1.column-1] = puyoResourceId(tsumoInfo.currentColor[0])
        val p2 = tsumoInfo.currentSubPos
        ret[p2.row][p2.column-1] = puyoResourceId(tsumoInfo.currentColor[1])
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
                    MainField(field, tsumoInfo, size)
                    BottomWall(size)
                }
                SideWall(size)
            }
            NextTsumoFrame(tsumoInfo, size)
        }
    }

    @Composable
    fun SliderFrame(
        onValueChange: (Float) -> Unit,
        index: Float,
        max: Int,
        modifier: Modifier = Modifier
    ) {
        Slider(
            value = index,
            onValueChange = onValueChange,
            modifier = modifier,
            steps = max(max-1, 0),
            valueRange = 0f..(max.toFloat()),
        )
    }

    @Composable
    fun FieldPicker(
        header: String,
        bases: List<Base>,
        onFieldClicked: (Base) -> Unit
    ) {
        val rows = 5
        val chunkedList = bases.chunked(rows)
        Column {
            Text(header, style = MaterialTheme.typography.h5)  // stickyHeaderとどう使い分ける？
            Divider()
            LazyColumn {
                items(chunkedList.size) { idx ->
                    Row {
                        chunkedList[idx].forEachIndexed { _, base ->
                            Card(
                                Modifier.background(Color.White, RoundedCornerShape(16.dp))
                                    .border(1.dp, Color.Black).clickable(onClick = {onFieldClicked(base)})
                            ) {
                                FieldPickerItem(base, onFieldClicked)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getColor(c: Char): Int {
        return when (c) {
            'r' -> R.drawable.pr
            'b' -> R.drawable.pb
            'g' -> R.drawable.pg
            'y' -> R.drawable.py
            'p' -> R.drawable.pp
            else -> R.drawable.blank
        }
    }
    private fun fieldStrToColors(str: String): Array<Array<Int>> {
        return Array(13) { i -> Array(6) { j -> getColor(str[i * 6 + j]) } }
    }

    @Composable
    private fun FieldPickerItem(base: Base, onClicked: (Base) -> Unit) {
        val numOfPlacement =  TsumoController.getNumOfPlacement(base.placementHistory)
        Column() {
            Text(
                if (base.allClear) {
                    "全消し"
                } else {
                    ""
                }
            )
            Text("${numOfPlacement}手目")
            Text("${base.point}点")

            val colors = fieldStrToColors(base.field)
            PuyoField(colors.reversed().toTypedArray(), 10.dp)

        }
        //Text(field.toString(), Modifier.clickable(onClick = { onClicked(field) }))
    }

    private fun getSeed(str: String) : Int?{
        return try {
            val seed = str.toInt()
            if (seed in 0..65536) seed
            else throw java.lang.NumberFormatException()
        } catch (e: java.lang.NumberFormatException) {
            null
        }
    }

    private fun isValidPattern(str: String) : Boolean{
        return str.map {it in "abcde"}.reduce {acc, it -> acc && it}
    }

    @Composable
    fun LoadPopupWindow(
        size: Dp,
        closeFun: () -> Unit,
        onShowSeedClick: (Int) -> MutableList<Base>,
        onShowPatternClick: (String) -> MutableList<Base>,
        onShowAllClick: () -> MutableList<Base>,
        onFieldClick: (Base) -> Unit,
    ) {
        val (bases, setBases) = remember { mutableStateOf(mutableListOf<Base>()) }
        val (seedError, setSeedError) = remember { mutableStateOf(false)}
        val (patternError, setPatternError) = remember { mutableStateOf(false)}
        Popup(
            alignment = Alignment.Center,
            properties = PopupProperties(focusable = true),
            onDismissRequest = closeFun
        ) {
            Card(
                Modifier.background(Color.White, RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Black)
            ) {
                Column () {
                    TextFieldWithButton(
                        size = size,
                        isError = seedError,
                        textLabel = "search by seed",
                        keyboardType = KeyboardType.Number,
                        onClick = {
                            val seed = getSeed((it.text))
                            if (seed == null) {
                                setSeedError(true)
                            } else {
                                setSeedError(false)
                                setBases(onShowSeedClick(seed))
                            }
                        }
                    )
                    TextFieldWithButton(
                        size = size,
                        isError = patternError,
                        textLabel = "search by pattern",
                        keyboardType = KeyboardType.Ascii,
                        onClick = {
                            if (isValidPattern(it.text)){
                                setPatternError(false)
                                setBases(onShowPatternClick(it.text))
                            } else {
                                setPatternError(true)
                            }
                        }
                    )
                    CallToActionButton(
                        text = "すべて表示",
                        onClick = {
                            setBases(onShowAllClick())
                        },
                    )
                    FieldPicker(
                        header = "fields:",
                        bases = bases,
                        onFieldClicked = {
                            onFieldClick(it)
                            closeFun()
                        },
                    )
                }
            }
        }
    }

    @Composable
    fun Home(presenter: HomePresenter)  {
        val sampleTsumoInfo = TsumoInfo(
            Array(2) {PuyoColor.RED},
            Array(2) {Array(2) {PuyoColor.RED}},
            3,
            Rotation.DEGREE0
        )
        val tsumoInfo = presenter.tsumoInfo.observeAsState(sampleTsumoInfo).value
        val currentField = presenter.currentField.observeAsState(Field()).value
        val seed = presenter.seed.observeAsState(0).value
        val historySliderValue = presenter.historySliderValue.observeAsState(0f).value
        val historySize = presenter.historySize.observeAsState(0).value
        val (showLoadPopup, setShowLoadPopup) = remember { mutableStateOf(false) }
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()

        Scaffold(
            scaffoldState = scaffoldState,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(5.dp)
                )
                {
                    Row(
                        modifier = Modifier.weight(6f)
                    ) {
                        if (showLoadPopup) {
                            LoadPopupWindow(
                                size = 60.dp,
                                closeFun = { setShowLoadPopup(false) },
                                onShowSeedClick = {
                                    presenter.searchBySeed(it, requireContext())
                                },
                                onShowPatternClick = {
                                    presenter.searchByPattern(it, requireContext())
                                },
                                onShowAllClick = {
                                    presenter.showAll(requireContext())
                                },
                                onFieldClick = presenter::load
                            )
                        }
                        FieldFrame(field = currentField, tsumoInfo = tsumoInfo, 20.dp)
                        Column(horizontalAlignment = Alignment.End) {
                            CurrentSeed(seed = seed)
                            FieldGeneration(
                                size = 60.dp,
                                onSeedGenClicked = presenter::setSeed,
                                onPatternGenClicked = {},
                                onRandomGenClicked = presenter::randomGenerate,
                            )
                            ChainInfoArea(field = currentField)
                            SaveLoad(
                                onLoadClick = {
                                    setShowLoadPopup(true)
                                },
                                onSaveClick = {
                                    presenter.save(requireContext())
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar("saved.")
                                    }
                                }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        SliderFrame(
                            index = max(historySliderValue, 0f),
                            max = max(historySize - 1, 0),
                            onValueChange = presenter::setHistoryIndex
                        )
                    }
                    Box(
                        modifier = Modifier.weight(3f)
                    ) {
                        TsumoControlButtonArea(
                            onAClick = presenter::rotateLeft,
                            onBClick = presenter::rotateRight,
                            onLeftClick = presenter::moveLeft,
                            onRightClick = presenter::moveRight,
                            onDownClick = {
                                presenter.dropDown(mActivity)
                            }
                        )
                    }
                }
            },
        )
    }

    @Composable
    fun MainApp(presenter: HomePresenter) {
        Home(presenter)
    }

    //@Preview
    //@Composable
    //fun PreviewGreeting() {
    //    //CallToAction button("SET SEED", mPresenter.set_seed(), )
    //    //MainField(field = Field())
    //    val sampleTsumoInfo = TsumoInfo(
    //        Array(2) {PuyoColor.RED},
    //        Array(2) {Array(2) {PuyoColor.RED}},
    //        3,
    //        Rotation.DEGREE0
    //    )
    //    val sampleField = Field.from("rrrbbbggg")
    //    //FieldFrame(field = sampleField, sampleTsumoInfo)
    //    //Home()
    //}

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        mRoot = root
        val activity = requireActivity()
        mActivity = activity
        mPresenter = HomePresenter(requireActivity().assets)

        return ComposeView(requireContext()).apply {
            setContent {
                MainApp(mPresenter)
            }
        }
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

    private fun getDotResource(color: PuyoColor): Int {
        return when (color) {
            PuyoColor.RED -> R.drawable.dotr
            PuyoColor.BLUE -> R.drawable.dotb
            PuyoColor.GREEN -> R.drawable.dotg
            PuyoColor.YELLOW -> R.drawable.doty
            PuyoColor.PURPLE -> R.drawable.dotp
            PuyoColor.EMPTY -> R.drawable.blank
            PuyoColor.DISAPPEAR -> R.drawable.blank
        }
    }

    // リストで渡された順に下から積み上げる
    private fun drawDot(column: Int, dots: List<PuyoColor>, field: Field, colors: Array<Array<Int>>) {
        var row = field.getHeight(column) + 1
        for (dot in dots) {
            if (row <= 13) {
                colors[row-1][column-1] = getDotResource(dot)
                row++
            }
        }
    }

    private fun dotColors(tsumoInfo: TsumoInfo, field: Field, colors: Array<Array<Int>>) : Array<Array<Int>> {
        // draw current
        val mainColor = tsumoInfo.currentColor[0]
        val subColor = tsumoInfo.currentColor[1]

        // draw dot
        when (tsumoInfo.rot) {
            Rotation.DEGREE0 -> drawDot(tsumoInfo.currentMainPos.column, listOf(mainColor, subColor), field, colors)
            Rotation.DEGREE90, Rotation.DEGREE270 -> {
                drawDot(tsumoInfo.currentMainPos.column, listOf(mainColor), field, colors)
                drawDot(tsumoInfo.currentSubPos.column, listOf(subColor), field, colors)
            }
            Rotation.DEGREE180 -> drawDot(tsumoInfo.currentMainPos.column, listOf(subColor, mainColor), field, colors)
        }
        return colors
    }

}
