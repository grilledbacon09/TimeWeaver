package com.example.timeweaver.screens

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.timeweaver.navigation.LocalNavGraphViewModelStoreOwner
import com.example.timeweaver.navigation.NavViewModel
import com.example.timeweaver.navigation.Routes

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(navController: NavHostController) {
    val navViewModel: NavViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
//    val navViewModel: NavViewModel =
//        viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)


    val formatter = navViewModel.formatter
    val date = navViewModel.date

    var yearState by navViewModel.yearState
    var monthState by navViewModel.monthState
    var dayState by navViewModel.dayState

    var list = listOf("모프 제안서","실습보고서 12주차","회의록 제출","코드 개발")
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

                }
            )

            Divider(
                modifier = Modifier
                    .height(1.dp)
                    .padding(horizontal = 15.dp)
            )
        }
        item {
            ListPlusButton {
                navController.navigate(Routes.CalendarPlus.route)
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
fun ListPlusButton(listPlus:()->Unit) {
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