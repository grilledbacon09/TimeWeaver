package com.example.timeweaver.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.timeweaver.navigation.LocalNavGraphViewModelStoreOwner
import com.example.timeweaver.navigation.NavViewModel
import com.example.timeweaver.navigation.Routes
import com.example.timeweaver.roomDB.FixedDatabase
import com.example.timeweaver.roomDB.FixedEntity
import com.example.timeweaver.roomDB.FixedRepository

@Composable
fun FixedScreen(navController: NavHostController) {
    val navViewModel: NavViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val day = listOf("일", "월", "화", "수", "목", "금", "토")
    val daylist = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    val context = LocalContext.current
    val database = FixedDatabase.getFixedDatabase(context)
    val fixedRepository = FixedRepository(database)
    val fixedEntities: LiveData<List<FixedEntity>> = fixedRepository.getAll()
    // Observe the LiveData using remember and collectAsState to get the latest data
    val fixedEntitiesState: List<FixedEntity> by fixedEntities.observeAsState(emptyList())

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //Text(text = "Fixed Task Screen")
        Box{
            Row(modifier = Modifier
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState)) {
                for (column in 0 until 8){
                    Column {
                        for (row in 0 until 25){
                            Box {
                                if (column == 0 && row == 0)
                                    TableCell(text = "", Color.White)
                                else if (row == 0)
                                    TableCell(text = "${day[column-1]}요일", Color.White)
                                else if (column == 0)
                                    TableCell(text = "${row}시", Color.White)
                                else {
                                    TableCell(text = "", Color.White)
//                                    navViewModel.fixedtasklist.forEach {
//                                        if(it.startTime >= row && it.startTime < row+1 && it.day == daylist[column-1]){
//                                            TableCell(text = it.name, Color.LightGray)
//                                        }else if (it.startTime < row && it.startTime+it.length > row && it.day == daylist[column-1]){
//                                            TableCell(text = it.name, Color.LightGray)
//                                        }
//                                    }
//                                    if (navViewModel.fixedTaskArray[row-1][column-1] != "")
//                                        TableCell(text = navViewModel.fixedTaskArray[row-1][column-1], color = Color.LightGray)
                                    if (fixedEntitiesState.any { (it.startH == row || ((it.startH < row) && (it.startH + it.duration - 1) >= row)) && it.days == daylist[column-1] })
                                        TableCell(text = fixedEntitiesState.find { (it.startH == row || ((it.startH < row) && (it.startH + it.duration - 1) >= row)) && it.days == daylist[column-1] }?.name ?: "", color = Color.LightGray)
                                }
                            }
                        }
                    }
                }
            }
            ListPlusButton2 {
                navController.navigate(Routes.AddFixedTask.route)
            }
        }

        Log.w("fixedList", "${navViewModel.fixedtasklist}")
    }
}

@Composable
fun TableCell(text: String, color: Color) {
    Box(
        modifier = Modifier
            .border(1.dp, Color.Black)
            .padding(3.dp)
            .width(50.dp)
            .height(50.dp)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}

@Composable
fun ListPlusButton2( listPlus: () -> Unit) {
    Box (modifier = Modifier.fillMaxSize()){
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .size(50.dp)
                .align(Alignment.BottomEnd),
            onClick = listPlus
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "plus work to list"
            )
        }

    }

}
