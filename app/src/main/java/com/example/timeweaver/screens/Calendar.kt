package com.example.timeweaver.screens

import android.util.Log
import android.widget.CalendarView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(navController: NavHostController) {
    val context = LocalContext.current
    val database = TodoDatabase.getItemDatabase(context)
    val todoRepository = TodoRepository(database)
    val navViewModel: NavViewModel = viewModel(LocalNavGraphViewModelStoreOwner.current)
    val todoEntities: LiveData<List<TodoEntity>> = todoRepository.getAll()
    val todoEntitiesState: List<TodoEntity> by todoEntities.observeAsState(emptyList())
    Log.d("todoentityState", "CalendarScreen: $todoEntitiesState")

    val formatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val date = remember { mutableStateOf(LocalDate.now()) }

    val yearState = remember { mutableStateOf(date.value.year.toString()) }
    val monthState = remember { mutableStateOf((date.value.monthValue).toString()) }
    val dayState = remember { mutableStateOf(date.value.dayOfMonth.toString()) }

    val selectedDateInt = "${yearState.value}${monthState.value.padStart(2, '0')}${dayState.value.padStart(2, '0')}".toInt()

    val filteredTodoEntities = todoEntitiesState.filter { it.deadline == selectedDateInt }

    val list = filteredTodoEntities.map { it.name }
    var checkBoxList by remember { mutableStateOf(list.map { false }.toMutableList()) }
    LaunchedEffect(checkBoxList) {
        if (filteredTodoEntities.isNotEmpty()) {
            filteredTodoEntities.forEachIndexed { index, todoEntity ->
                if (todoEntity.completed != checkBoxList[index]) {
                    todoEntity.completed = checkBoxList[index]
                    navViewModel.updateItem(todoEntity)
                    Log.d("DatabaseUpdate", "TodoEntity: ${todoEntity.name}, Completed: ${todoEntity.completed}")
                }
            }
        }
    }
    LazyColumn(
        modifier = Modifier.padding(15.dp),
        verticalArrangement = Arrangement.Center
    ) {
        item {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { CalendarView(it) }
            ) { calendarView ->
                val selectedDate = "${yearState.value}-${monthState.value}-${dayState.value}"
                calendarView.date = formatter.parse(selectedDate)!!.time

                calendarView.setOnDateChangeListener { _, year, month, day ->
                    yearState.value = year.toString()
                    monthState.value = (month + 1).toString()
                    dayState.value = day.toString()
                }
            }
        }

        stickyHeader {
            ScheduleHeader(
                month = monthState.value,
                day = dayState.value,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Display tasks with the selected date as the deadline
        itemsIndexed(filteredTodoEntities) { _, todoEntity ->
            ScheduleItem(
                item = todoEntity.name,
                navViewModel = navViewModel,
                todoEntity = todoEntity,
            )
            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .padding(horizontal = 15.dp)
            )
        }

        item {
            if (filteredTodoEntities.isEmpty()) {
                Text(text = "No tasks for this date")
            }
        }


        item {
            val formattedDate = "${yearState.value}${monthState.value.padStart(2, '0')}${dayState.value.padStart(2, '0')}"
            ListPlusButton(monthState.value, dayState.value, formattedDate) { monthState, dayState, date ->
                navController.navigate(Routes.CalendarPlus.createRoute(monthState, dayState, date))
            }
        }
    }
}

@Composable
fun ScheduleItem(
    item: String,
    todoEntity: TodoEntity,
    navViewModel: NavViewModel
) {

    var checked by remember {
        mutableStateOf(todoEntity.completed)
    }

    if(checked != todoEntity.completed) checked = !checked

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = {
                checked = !checked
                todoEntity.completed = !todoEntity.completed
                navViewModel.updateItem(todoEntity)
            }
        )
        Text(
            text = item,
            textDecoration = if (todoEntity.completed) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}


@Composable
fun ScheduleHeader(month: String, day: String, modifier: Modifier) {
    Text(text = "Schedule for " +month+ "월 " +day+ "일",
        style = TextStyle(
            fontSize = 24.sp // 글자 크기를 24sp로 설정
        )
    )

}

@Composable
fun ListPlusButton(month:String,day:String,date: String, listPlus: (String,String,String) -> Unit) {
    Box (modifier = Modifier.fillMaxSize()){
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .size(50.dp)
                .align(Alignment.BottomEnd),
            onClick = {listPlus(month,day,date)}
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "plus work to list"
            )
        }

    }

}
