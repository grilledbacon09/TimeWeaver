package com.example.timeweaver.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.timeweaver.navigation.LocalNavGraphViewModelStoreOwner
import com.example.timeweaver.navigation.NavViewModel
import com.example.timeweaver.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFixedTask(navController: NavController) {

    val navViewModel: NavViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    var taskName by remember {
        mutableStateOf("")
    }

    var taskTime by remember {
        mutableStateOf("")
    }


    val timePickerState = rememberTimePickerState(
        initialHour = 0,
        initialMinute = 0,
        is24Hour = true
    )

    val checked = remember {
        mutableStateListOf(false, false, false, false, false, false, false)
    }

    val day = listOf("일", "월", "화", "수", "목", "금", "토")
    var daylist = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(text = "주마다 반복되는 일정 추가", fontSize = 30.sp)
        OutlinedTextField(value = taskName,  onValueChange = { taskName = it }, label = { Text(text = "일정 명 입력") }, keyboardActions = KeyboardActions())
        TimeInput(state = timePickerState)//추후 dropdownmenu로 수정하는 편이 좋을듯?
        OutlinedTextField(value = taskTime,  onValueChange = { taskTime = it }, label = { Text(text = "소요 시간 입력(분)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Row {

            for (i:Int in 0..6) {
                Column {
                    Text(text = day[i])
                    Checkbox(checked = checked[i], onCheckedChange = { checked[i] = it })
                }
            }
        }

        Button(onClick = {
            for (i:Int in 0..6){
                if (checked[i] && taskName != "" && taskTime.toInt() > 0){
                    val fixedTask = FixedTask(taskName, navViewModel.fixedtasklist.size, daylist[i], timePickerState.hour*60+timePickerState.minute, taskTime.toInt())
                    navViewModel.fixedtasklist.add(fixedTask)
                }
            }
            navController.navigate(Routes.Fixed.route)
        }) {
            Text(text = "추가하기")
        }
    }
}
