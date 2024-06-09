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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.timeweaver.navigation.LocalNavGraphViewModelStoreOwner
import com.example.timeweaver.navigation.NavViewModel
import com.example.timeweaver.navigation.Routes

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

    val scheduleArray = Array(24) { Array(7) { "" } }

    navViewModel.scheduledtasklist.add(ScheduledTask("test1", 0, "Mon", 13, 2))
    navViewModel.scheduledtasklist.add(ScheduledTask("test2", 1, "Mon", 22, 4))
    navViewModel.scheduledtasklist.add(ScheduledTask("test3", 2, "Sat", 22, 4))

    val scheduleList = navViewModel.scheduledtasklist

    //알고리즘 돌 때 처리했으면 좋겠음
//    scheduleList.forEach{
//        val startTime = it.startTime-1
//        val endTime = startTime + it.length
//
//        for (j:Int in startTime..<endTime){
//            if (j < 24){
//                scheduleArray[j][dayMap[it.day]!!] = it.name
//            }else {
//                if (it.day == "Sat") {
//                    scheduleArray[j-24][0] = it.name
//                } else {
//                    scheduleArray[j-24][dayMap[it.day]?.plus(1)!!] = it.name
//                }
//            }
//        }
//
//    }

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
                                    if (navViewModel.fixedTaskArray[row - 1][column - 1] != "")
                                        TableCell(
                                            text = navViewModel.fixedTaskArray[row - 1][column - 1],
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