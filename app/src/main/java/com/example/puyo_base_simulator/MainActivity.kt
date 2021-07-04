package com.example.puyo_base_simulator


import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.puyo_base_simulator.ui.components.*
import com.example.puyo_base_simulator.data.ChainInfo
import com.example.puyo_base_simulator.data.Field
import com.example.puyo_base_simulator.ui.home.HomePresenter
import kotlinx.coroutines.launch
import kotlin.math.max

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@ExperimentalComposeUiApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp(HomePresenter(assets, dataStore), this, this)
        }
    }


    @Composable
    fun ChainInfoArea(chainInfo: ChainInfo) {
        val puyoNum = chainInfo.disappearPuyo
        Text("${chainInfo.bonus} * ${puyoNum * 10} = ${chainInfo.bonus * puyoNum * 10}")
        Text("連鎖合計: ${chainInfo.chainPoint}")
        Text("試合合計: ${chainInfo.accumulatedPoint}")
        if (chainInfo.chainNum != 0) {
            Text("${chainInfo.chainNum}連鎖")
        } else {
            Text("")
        }
    }

    @Composable
    fun SaveLoad(
        onSaveClick: () -> Unit,
        onLoadClick: () -> Unit,
        enabled: Boolean = true,
    ) {
        Row(
            modifier = Modifier.padding(5.dp)
        ) {
            ActionButton(
                text = "SAVE",
                onClick = onSaveClick,
                enabled = enabled,
            )
            ActionButton(
                text = "LOAD",
                onClick = onLoadClick,
                enabled = enabled,
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
        enabled: Boolean,
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(5.dp),
        ) {
            SeedInputField(
                size = size,
                onClick = onSeedGenClicked,
                textLabel = "generate by seed",
                enabled = enabled
            )
            PatternInputField(
                size = size,
                onClick = onPatternGenClicked,
                textLabel = "generate by pattern",
                enabled = enabled
            )
            ActionButton(
                text = "generate randomly",
                onClick = onRandomGenClicked,
                enabled = enabled,
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
    fun HistoryControlArea(
        onUndoClick: () -> Unit,
        onRedoClick: () -> Unit,
        onSliderChange: (Float) -> Unit,
        sliderValue: Float,
        max: Int,
        size: Dp,
        enabled: Boolean,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                ActionIcon(
                    icon = Icons.Filled.Undo,
                    size = size,
                    enabled = enabled,
                    onClick = onUndoClick
                )
                ActionIcon(
                    icon = Icons.Filled.Redo,
                    size = size,
                    enabled = enabled,
                    onClick = onRedoClick
                )
            }
            SliderFrame(
                index = sliderValue,
                max = max,
                onValueChange = onSliderChange,
                enabled = enabled,
            )
        }

    }

    @Composable
    fun Home(
        presenter: HomePresenter,
        context: Context,
        activity: Activity,
        navController: NavController
    ) {
        val tsumoInfo = presenter.tsumoInfo.observeAsState(presenter.emptyTsumoInfo).value
        val currentField = presenter.currentField.observeAsState(Field()).value
        val seed = presenter.seed.observeAsState(0).value
        val historySliderValue = presenter.historySliderValue.observeAsState(0f).value
        val historySize = presenter.historySize.observeAsState(0).value
        val (showLoadPopup, setShowLoadPopup) = remember { mutableStateOf(false) }
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()
        val duringChain = presenter.duringChain.observeAsState(false).value
        val chainInfo = presenter.chainInfo.observeAsState(ChainInfo(0, 0, 0, 0, 0)).value
        val showDoubleNext by presenter.showDoubleNext.collectAsState()

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                HomeAppBar(onSettingsClick = { navController.navigate("settings") })
            },
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
                                    presenter.searchBySeed(it, context)
                                },
                                onShowPatternClick = {
                                    presenter.searchByPattern(it, context)
                                },
                                onShowAllClick = {
                                    presenter.showAll(context)
                                },
                                onFieldClick = presenter::load
                            )
                        }
                        FieldFrame(
                            field = currentField,
                            tsumoInfo = tsumoInfo,
                            20.dp,
                            duringChain = duringChain,
                            showDoubleNext
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            CurrentSeed(seed = seed)
                            FieldGeneration(
                                size = 60.dp,
                                onSeedGenClicked = presenter::setSeed,
                                onPatternGenClicked = presenter::generateByPattern,
                                onRandomGenClicked = presenter::randomGenerate,
                                enabled = !duringChain,
                            )
                            ChainInfoArea(chainInfo)
                            SaveLoad(
                                onLoadClick = {
                                    setShowLoadPopup(true)
                                },
                                onSaveClick = {
                                    presenter.save(context)
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar("saved.")
                                    }
                                },
                                enabled = !duringChain,
                            )
                        }
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        HistoryControlArea(
                            onUndoClick = presenter::undo,
                            onRedoClick = { presenter.redo(activity) },
                            onSliderChange = presenter::setHistoryIndex,
                            sliderValue = max(historySliderValue, 0f),
                            max = max(historySize - 1, 0),
                            size = 40.dp,
                            enabled = !duringChain,
                        )
                        if (duringChain) {
                            ActionIcon(
                                icon = Icons.Filled.DoubleArrow,
                                size = 40.dp,
                                enabled = true,
                                onClick = presenter::fastenChainSpeed
                            )
                        }
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
                                presenter.dropDown(activity)
                            },
                            size = 80.dp,
                            enabled = !duringChain
                        )
                    }
                }
            },
        )
    }


    @Composable
    fun Settings(navController: NavController, presenter: HomePresenter) {
        val showDoubleNext by presenter.showDoubleNext.collectAsState()
        Scaffold(
            topBar = {
                SettingAppBar(onBackClick = { navController.navigate("home") })
            },
            content = {
                Column {
                    Row {
                        Text("ねくねくを表示")
                        Checkbox(
                            checked = showDoubleNext,
                            onCheckedChange = { presenter.updateShowDoubleNext(it) })
                    }
                }
            }
        )
    }

    @Composable
    fun MainApp(presenter: HomePresenter, context: Context, activity: Activity) {
        val navController = rememberNavController()
        val colors = lightColors(
            //surface = Color(0xffd5d5d5),
            secondary = Color(0xFFB0D169),
            background = Color(0xffffffff),
            primary = Color(0xFFB0D169),
            //primary = Color(0xff94d0ff)
        )
        MaterialTheme(
            colors = colors
        ) {
            NavHost(navController, startDestination = "home") {
                composable("settings") { Settings(navController, presenter) }
                composable("home") { Home(presenter, context, activity, navController) }
            }
        }
    }
}