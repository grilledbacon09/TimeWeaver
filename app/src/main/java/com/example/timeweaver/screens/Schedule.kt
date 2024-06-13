package com.example.timeweaver.screens

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.timeweaver.navigation.LocalNavGraphViewModelStoreOwner
import com.example.timeweaver.navigation.NavViewModel
import com.example.timeweaver.navigation.Routes
import com.example.timeweaver.roomDB.FixedDatabase
import com.example.timeweaver.roomDB.FixedEntity
import com.example.timeweaver.roomDB.FixedRepository

@Composable
fun ScheduleScreen(navController: NavHostController) {
    val navViewModel: NavViewModel =
        viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val day = listOf("일", "월", "화", "수", "목", "금", "토")
    val daylist = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val dayMap = mapOf(
        "Sun" to 0,
        "Mon" to 1,
        "Tue" to 2,
        "Wed" to 3,
        "Thu" to 4,
        "Fri" to 5,
        "Sat" to 6
    )
    val fixedTaskArray = Array(24){ Array(7) { "" } }
    val scheduleArray = Array(24) { Array(7) { "" } }

    val context = LocalContext.current
    val database = FixedDatabase.getFixedDatabase(context)
    val fixedRepository = FixedRepository(database)
    val fixedEntities: LiveData<List<FixedEntity>> = fixedRepository.getAll()
    // Observe the LiveData using remember and collectAsState to get the latest data
    val fixedEntitiesState: List<FixedEntity> by fixedEntities.observeAsState(emptyList())

    fixedEntitiesState.forEach {
        if (it.startH + it.duration - 1 > 23){//날짜가 넘어가면
            if (it.days == "Sat"){ // 토-일로 넘어가면
                for (i:Int in it.startH-1..<24){
                    fixedTaskArray[i][dayMap[it.days]!!] = it.name
                }
                for (i:Int in 0..<it.startH+it.duration-24-1){
                    fixedTaskArray[i][0] = it.name
                }
            }else{ // 아니면
                for (i:Int in it.startH-1..<24){
                    fixedTaskArray[i][dayMap[it.days]!!] = it.name
                }
                for (i:Int in 0..<it.startH+it.duration-24-1){
                    fixedTaskArray[i][dayMap[it.days]!!+1] = it.name
                }
            }
        }else{//아니면
            for (i:Int in it.startH-1..<it.startH+it.duration){
                fixedTaskArray[i][dayMap[it.days]!!] = it.name
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //Text(text = "Fixed Task Screen")
        Box {
            Row(modifier = Modifier.verticalScroll(verticalScrollState).horizontalScroll(horizontalScrollState)) {
                for (column in 0 until 8) {
                    Column {
                        for (row in 0 until 25) {
                            Box {
                                if (column == 0 && row == 0)
                                    TableCell(text = "", Color.White)
                                else if (row == 0)
                                    TableCell(text = "${day[column - 1]}요일", Color.White)
                                else if (column == 0)
                                    TableCell(text = "${row}시", Color.White)
                                else {
                                    TableCell(text = "", Color.White)
                                    if (fixedTaskArray[row - 1][column - 1] != "")
                                        TableCell(
                                            text = fixedTaskArray[row - 1][column - 1],
                                            color = Color.LightGray
                                        )
                                    else if (scheduleArray[row-1][column-1] != "")
                                        TableCell(
                                            text = scheduleArray[row-1][column-1],
                                            color = Color.Red
                                        )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}