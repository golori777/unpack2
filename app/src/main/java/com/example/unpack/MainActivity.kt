package com.example.unpack

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.unpack.MainActivity.Companion.currentText
import com.example.unpack.MainActivity.Companion.dao
import com.example.unpack.database.MemoDao
import com.example.unpack.database.Memo
import com.example.unpack.database.UnpackDatabase
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : ComponentActivity() {

    companion object {
        lateinit var db : UnpackDatabase
        lateinit var dao : MemoDao
        lateinit var currentText : String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            db = Room.databaseBuilder(this.applicationContext, UnpackDatabase::class.java, "unpack-database").allowMainThreadQueries().build()
            dao = db.memoDao()
            currentText = ""
            val navController = rememberNavController()
            Scaffold(
                topBar = { TopAppBar() },
            ) {
                NavigationComponent(navController)
            }
        }
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
        composable("details") {
            DetailScreen(navController)
        }
    }
}

// ホーム画面
@Composable
fun HomeScreen(navController: NavHostController) {

    var memos = dao.getAllMemos()

    Scaffold(
        floatingActionButton = { AddFab(navController) }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()) // スクロール設定
        ) {
            memos.forEach{ memo ->
                Card(
                    modifier = Modifier.border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(10.dp))
                ) {
                    Row(
                        Modifier
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = "お気に入り")
                        Image(
                            modifier = Modifier.size(width = 100.dp, height = 100.dp),
                            painter = painterResource(R.drawable.ap_parrot),
                            contentDescription = "Contact profile picture",
                        )
                        Column() {
                            Text(
                                modifier = Modifier.fillMaxWidth().size(width = 100.dp, height = 50.dp),
                                text = memo.text
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
fun DetailScreen(navController: NavHostController) {
    Scaffold(
        floatingActionButton = { SaveFab(navController) }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            var name by remember { mutableStateOf("") }

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    currentText = name
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
fun TopAppBar() {
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
        navController.navigate("details")
    }) {
        Icon(Icons.Filled.Add, contentDescription = "追加")
    }
}

// 保存ボタン
@Composable
fun SaveFab(navController: NavHostController) {
    FloatingActionButton(onClick = {
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()
        dao.insert(Memo(
            id = 0,
            text = currentText,
            updateDate = df.format(date),
        ))
        currentText = ""
        navController.navigate("home")
    }) {
        Icon(Icons.Filled.Check, contentDescription = "保存")
    }
}

// 一覧表示
@Composable
fun ListDisplay(name: String, ary : List<String>) {
    Column() {
        ary.forEach{ ary ->
            dispRow(name = ary)
            Divider()
        }
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
            Icon(Icons.Filled.Favorite, contentDescription = "お気に入り")
            Image(
                painter = painterResource(R.drawable.ap_parrot),
                contentDescription = "Contact profile picture",
            )
            Text(text = "Hello $name!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val navController = rememberNavController()
    DetailScreen(navController)
}

