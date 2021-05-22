package com.example.puyo_base_simulator.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.puyo_base_simulator.ui.components.*
import kotlinx.coroutines.launch
import kotlin.math.max

@ExperimentalComposeUiApi
class HomeFragment : Fragment() {

    @Composable
    fun ChainInfoArea(field: Field) {
        val puyoNum = field.disappearPuyo.size
        Text("${field.bonus} * ${puyoNum*10} = ${field.bonus*puyoNum*10}")
        Text("連鎖の合計: ${field.chainPoint}")
        Text("試合の合計: ${field.accumulatedPoint}")
        Text("${field.chainNum}連鎖")
    }

    @Composable
    fun SaveLoad(
        onSaveClick: () -> Unit,
        onLoadClick: () -> Unit,
    ) {
        Row(
            modifier = Modifier.padding(5.dp)
        ) {
            ActionButton(
                text = "SAVE",
                onClick = onSaveClick,
            )
            ActionButton(
                text = "LOAD",
                onClick = onLoadClick,
            )
        }
    }

    // いい感じに大きさを決めたい
    @Composable
    fun FieldGeneration(
        size: Dp,
        onSeedGenClicked: (Int) -> Unit,
        onPatternGenClicked: (String) -> Unit,
        onRandomGenClicked: () -> Unit,
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(5.dp),
        ) {
            SeedInputField(size = size, onClick = onSeedGenClicked, textLabel = "generate by seed")
            PatternInputField(size = size, onClick = onPatternGenClicked, textLabel = "generate by pattern")
            ActionButton(
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

    @Composable
    fun Home(presenter: HomePresenter)  {
        val tsumoInfo = presenter.tsumoInfo.observeAsState(presenter.emptyTsumoInfo).value
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
                                onPatternGenClicked = presenter::generateByPattern,
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
                                presenter.dropDown(requireActivity())
                            },
                            size = 80.dp,
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

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MainApp(HomePresenter(requireActivity().assets))
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
    }

}
