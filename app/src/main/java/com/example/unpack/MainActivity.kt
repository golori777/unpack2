package com.example.unpack

import android.icu.text.CaseMap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.os.bundleOf
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
//import com.example.unpack.MainActivity.Companion.currentText
import com.example.unpack.MainActivity.Companion.dao
import com.example.unpack.MainActivity.Companion.memoId
import com.example.unpack.database.MemoDao
import com.example.unpack.database.Memo
import com.example.unpack.database.UnpackDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


class MainActivity : ComponentActivity() {

    companion object {
        lateinit var db : UnpackDatabase
        lateinit var dao : MemoDao
        var memoId by Delegates.notNull<Int>()
//        lateinit var currentText : String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(this.applicationContext, UnpackDatabase::class.java, "unpack-database").allowMainThreadQueries().build()
        dao = db.memoDao()
//        currentText = ""
        memoId = 0
        setContent {
            val navController = rememberNavController()
            Scaffold(
//                topBar = { TopAppBarBase() },
            ) {
                NavigationComponent(navController)
            }
        }
    }
    override fun onBackPressed() {
    }
}

// 画面一覧
@Composable
fun NavigationComponent(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController)
        }
        composable(
            "details/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            it.arguments?.getString("id")?.let { it
                DetailScreen(navController, it)
            }
        }
    }
}


// ホーム画面
@Composable
fun HomeScreen(navController: NavHostController) {

    var memos = dao.getAllMemos()
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = { AddFab(navController) },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "unpack")
                },
            )
        }
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState()) // スクロール設定
        ) {
            memos.forEach{ memo ->
                Card(
                    Modifier
                        .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(10.dp))
                        .clickable {
                            memoId = memo.id
                            navController.navigate("details/$memoId")
                            Toast
                                .makeText(
                                    context,
                                    "Id is $memoId",
                                    Toast.LENGTH_SHORT 
                                )
                                .show()
                        }
                ) {
                    Row(
                        Modifier
                            .padding(8.dp)
                    ) {
                        Column() {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .size(width = 100.dp, height = 50.dp),
                                text = memo.text,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
//                                modifier = Modifier.size(width = 0.dp, height = 20.dp).fillMaxWidth(),
                                text = "update：" + memo.updateDate
                            )
                        }
                    }
                }
            }
        }
    }
}

// テキスト編集画面
@Composable
fun DetailScreen(navController: NavHostController, id: String) {
    var localText by remember { mutableStateOf("") }
    if (id != null) {
        val memo = dao.findById(id)
        memo?.text?.let {
            localText = it
        }
    }

    Scaffold(
//        floatingActionButton = {
//            Row(modifier = Modifier.padding(8.dp)) {
//                SaveFab(navController)
//                BackdFab(navController)
//                DeleteFab(navController)
//            }
//        },
        topBar = {
            TopAppBar {
                IconButton(onClick = {
                    val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                    val date = Date()

                    if (localText != "") {
                        if (memoId == 0) {
                            dao.insert(
                                Memo(
                                    id = 0,
                                    text = localText,
                                    updateDate = df.format(date),
                                )
                            )
                        } else {
                            dao.update(
                                Memo(
                                    id = memoId,
                                    text = localText,
                                    updateDate = df.format(date),
                                )
                            )
                        }
                    }
                    memoId = 0
//                    currentText = ""
                    navController.navigate("home")
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "戻る（保存）")
                }
                Text(text = "memo detail")
                IconButton(
                    onClick = {
                        if (memoId > 0) {
                            dao.deleteById(memoId)
                        }
                        navController.navigate("home")
                    },
//                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "削除")
                }
            }
        }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
//            currentText = text

            OutlinedTextField(
                value = localText,
                onValueChange = {
                    localText = it
//                    currentText = localText
                                },
                label = { Text("Memo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            )
        }
    }
}

@Composable
fun TopAppBarBase() {
    TopAppBar(
        title = {
            Text(text = "unpack")
        },
    )
}

// 追加ボタン
@Composable
fun AddFab(navController: NavHostController) {
    FloatingActionButton(onClick = {
        navController.navigate("details/" + null)
    }) {
        Icon(Icons.Filled.Add, contentDescription = "追加")
    }
}

// 保存ボタン
@Composable
fun SaveFab(navController: NavHostController) {
    FloatingActionButton(
        onClick = {
            val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val date = Date()

//            if (memoId == 0) {
//                dao.insert(Memo(
//                    id = 0,
//                    text = currentText,
//                    updateDate = df.format(date),
//                ))
//            } else {
//                dao.update(
//                    Memo(
//                        id = memoId,
//                        text = currentText,
//                        updateDate = df.format(date),
//                    )
//                )
//            }
//            memoId = 0
//            currentText = ""
            navController.navigate("home")
        }
    ) {
        Icon(Icons.Filled.Check, contentDescription = "保存")
    }
}

// 戻るボタン
@Composable
fun BackdFab(navController: NavHostController) {
    FloatingActionButton(onClick = {
        navController.navigate("home")
    }) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "保存")
    }
}

// 削除ボタン
@Composable
fun DeleteFab(navController: NavHostController) {
    FloatingActionButton(onClick = {
        if (memoId > 0) {
            dao.deleteById(memoId)
        }
        navController.navigate("home")
    }) {
        Icon(Icons.Filled.Delete, contentDescription = "保存")
    }
}


// レコード表示
@Composable
fun dispRow(name: String) {
    BoxWithConstraints {
        val screenWidth = with(LocalDensity.current) { constraints.maxWidth.toDp() }
        val screenHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() }
        Row(
            Modifier
                .size(width = screenWidth, height = 100.dp)
                .padding(8.dp)
        ) {
            Text(text = "Hello $name!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val navController = rememberNavController()
    DetailScreen(navController, "1")
}

