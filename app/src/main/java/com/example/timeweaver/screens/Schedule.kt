package com.example.timeweaver.screens

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.timeweaver.navigation.LocalNavGraphViewModelStoreOwner
import com.example.timeweaver.navigation.NavViewModel
import com.example.timeweaver.navigation.Routes
import com.example.timeweaver.roomDB.FixedDatabase
import com.example.timeweaver.roomDB.FixedEntity
import com.example.timeweaver.roomDB.FixedRepository
import com.example.timeweaver.roomDB.TodoDatabase
import com.example.timeweaver.roomDB.TodoEntity
import com.example.timeweaver.roomDB.TodoRepository

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
    val scheduleArray = navViewModel.scheduleArray

    val context = LocalContext.current
    val database = FixedDatabase.getFixedDatabase(context)
    val fixedRepository = FixedRepository(database)
    val fixedEntities: LiveData<List<FixedEntity>> = fixedRepository.getAll()
    // Observe the LiveData using remember and collectAsState to get the latest data
    val fixedEntitiesState: List<FixedEntity> by fixedEntities.observeAsState(emptyList())

    val todoDatabase = TodoDatabase.getItemDatabase(context)
    val todoRepository = TodoRepository(todoDatabase)
    val todoEntities: LiveData<List<TodoEntity>> = todoRepository.getAll()
    val todoEntitiesState: List<TodoEntity> by todoEntities.observeAsState(emptyList())

    fixedEntitiesState.forEach {
        Log.w("fixedTask", "$it")
        if (it.startH + it.duration > 23){//날짜가 넘어가면
            if (it.days == "Sat"){ // 토-일로 넘어가면
                for (i:Int in it.startH..<24){
                    fixedTaskArray[i][dayMap[it.days]!!] = it.name
                }
                for (i:Int in 0..<it.startH+it.duration-24){
                    fixedTaskArray[i][0] = it.name
                }
            }else{ // 아니면
                for (i:Int in it.startH..<24){
                    fixedTaskArray[i][dayMap[it.days]!!] = it.name
                }
                for (i:Int in 0..<it.startH+it.duration-24){
                    fixedTaskArray[i][dayMap[it.days]!!+1] = it.name
                }
            }
        }else{//아니면
            for (i:Int in it.startH..<it.startH+it.duration){
                fixedTaskArray[i][dayMap[it.days]!!] = it.name
            }
        }
    }

    var scheduleIDs = Array(7){ 0 }
    var red = 0xFF
    var green = 0x80
    var blue = 0

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
                                    TableCell(text = "${row-1}시", Color.White)
                                else {
                                    TableCell(text = "", Color.White)
                                    if (fixedTaskArray[row - 1][column - 1] != "")
                                        TableCell(
                                            text = fixedTaskArray[row - 1][column - 1],
                                            color = Color.LightGray
                                        )
                                    else if (scheduleArray[row-1][column-1][0] != "") {
                                        if (scheduleIDs[column - 1] != scheduleArray[row-1][column-1][1].toInt()){
                                            red = (red + 150) % 256
                                            green = (green + 100) % 256
                                            blue = (blue + 50) % 256
                                        }
                                        scheduleIDs[column - 1] = scheduleArray[row-1][column-1][1].toInt()
                                        TableCell(
                                            text = scheduleArray[row - 1][column - 1][0],
                                            color = Color(0xFF000000 + red.shl(16) + green.shl(8) + blue)
                                        )
                                    }
                                }
                            }
                        }
                        red = (red + 50) % 256
                        green = (green + 30) % 120
                        blue = (blue + 10) % 50
                    }
                }
            }

            RenewSchedule {
                navViewModel.tasklist.clear()
                navViewModel.fixedtasklist.clear()
                todoEntitiesState.forEach {
                    navViewModel.tasklist.add(Task(it.name, it.id, it.importance, it.completed, it.once, it.deadline, it.timeH))
                }
                fixedEntitiesState.forEach {
                    navViewModel.fixedtasklist.add(FixedTask(it.name, it.itemId, it.days, it.startH, it.duration))
                }


                navViewModel.scheduledtasklist.clear()
                navViewModel.freetimelist.clear()
                navViewModel.updateImportance()
                navViewModel.createSchedule()
                navViewModel.fillSchedule()

                navViewModel.tasklist.clear()
                navViewModel.fixedtasklist.clear()
                navController.navigate(Routes.Schedule.route){
                    launchSingleTop = true
                }
            }

        }
    }
}


@Composable
fun RenewSchedule(renew: () -> Unit) {
    Box (modifier = Modifier.fillMaxSize()){
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .size(50.dp)
                .align(Alignment.BottomEnd),
            onClick = renew
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "refreshes schedule"
            )
        }
    }
}