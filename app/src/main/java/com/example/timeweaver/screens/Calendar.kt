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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(navController: NavHostController) {
    val navViewModel: NavViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
//    val navViewModel: NavViewModel =
//        viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val context = LocalContext.current
    val database = TodoDatabase.getItemDatabase(context)
    val todoRepository = TodoRepository(database)
    val todoEntities: LiveData<List<TodoEntity>> = todoRepository.getAll()
    // Observe the LiveData using remember and collectAsState to get the latest data
    val todoEntitiesState: List<TodoEntity> by todoEntities.observeAsState(emptyList())


    val formatter = navViewModel.formatter
    val date = navViewModel.date

    var yearState by navViewModel.yearState
    var monthState by navViewModel.monthState
    var dayState by navViewModel.dayState

//    var list = listOf("모프 제안서","실습보고서 12주차","회의록 제출","코드 개발")
//    val list = navViewModel.tasklist.map { it.name }
    // 선택한 날짜를 YYYYMMDD 형식의 int 값으로 변환
    val selectedDateInt = "$yearState${monthState.padStart(2, '0')}${dayState.padStart(2, '0')}".toInt()

    // 선택한 날짜와 같은 deadline을 갖는 Task들을 필터링
    
    //여기를 통해서 하려 했는데 계속 오류가 뜨네요
//    if (todoEntitiesState.isNotEmpty()) {
//        navViewModel.tasklist.clear()
//        for (todoEntity in todoEntitiesState) {
//            val name = todoEntity.name
//            val id = todoEntity.id
//            val importance = todoEntity.importance
//            val completed = todoEntity.completed
//            val once = todoEntity.once
//            val deadline = todoEntity.deadline
//            val time = todoEntity.timeH
//            val task = Task(name, id, importance, completed, once, deadline, time)
//            navViewModel.tasklist.add(task)
//        }
//    }
    val filteredTasks = navViewModel.tasklist.filter { it.deadline == selectedDateInt }

    val list =filteredTasks.map{it.name}
    var checkBoxList by remember { mutableStateOf(list.map { false }.toMutableList()) }
//    Column {
//        Text(navViewModel.tasklist[0].name)
//    }

    LazyColumn (modifier = Modifier.padding(15.dp),
        verticalArrangement = Arrangement.Center){
        item {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { CalendarView(it) }
            ) { calendarView ->
                val selectedDate = "${yearState}-${monthState}-${dayState}"
                calendarView.date = formatter.parse(selectedDate)!!.time
                //calendarview.date를 사용하려면 자료형이 Long Type이어야 함
                //parse를 이용해 String을 Date로 변경
                //time을 이용해 Long Type으로 변경

                calendarView.setOnDateChangeListener { _, year, month, day ->
                    navViewModel.yearState.value = year.toString()
                    navViewModel.monthState.value = (month + 1).toString()
                    navViewModel.dayState.value = day.toString()
                }//State 추가한 이유 : LazyColumn으로 화면을 아래로 Scroll 하면 날짜 다시 초기화 됨

            }
        }


        stickyHeader {
            ScheduleHeader(month = monthState,day = dayState,
                modifier = Modifier.padding( bottom = 20.dp))
        }
        item {
            Spacer(modifier = Modifier.height(16.dp)) // 16dp의 공간 추가
        }


        items(list.size) {index->
            var checked by remember { mutableStateOf(false) }
            ScheduleItem(
                item = list[index].toString(),
                checked = checkBoxList[index],
                modifier = Modifier,
                onCheckBoxClick = {
                    checkBoxList = checkBoxList.toMutableList().also { it[index] = !it[index] }
                    // 체크박스 상태 변경 시, tasklist의 해당 Task 객체의 completed 속성 업데이트
                    val task = filteredTasks[index]
                    task.completed = checkBoxList[index]
                    //Log.d("completed","Task '${task.name}' completed: ${task.completed}")

                }
            )

            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .padding(horizontal = 15.dp)
            )
        }
        item {
            val formattedDate = "${yearState}${monthState.padStart(2, '0')}${dayState.padStart(2, '0')}"
            ListPlusButton(monthState,dayState,formattedDate) { monthstate,dayState,date ->
                navController.navigate(Routes.CalendarPlus.createRoute(monthstate,dayState,date))
            }
        }

    }

}

@Composable
fun ScheduleItem(item:String,checked: Boolean, modifier: Modifier,onCheckBoxClick: () -> Unit) {
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically){
        Checkbox(
            checked = checked,
            onCheckedChange = {onCheckBoxClick()})
        Text(text = item,
            textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None)
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
