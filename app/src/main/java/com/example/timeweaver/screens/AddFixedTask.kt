package com.example.timeweaver.screens


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.InvalidationTracker
import com.example.timeweaver.navigation.LocalNavGraphViewModelStoreOwner
import com.example.timeweaver.navigation.NavViewModel
import com.example.timeweaver.navigation.Routes
import com.example.timeweaver.roomDB.FixedDatabase
import com.example.timeweaver.roomDB.FixedRepository
import com.example.timeweaver.roomDB.FixedEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFixedTask(navController: NavController) {

    val context = LocalContext.current

    val navViewModel: NavViewModel =
        viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    val database = FixedDatabase.getFixedDatabase(context)
    val fixedRepository = FixedRepository(database)

    var taskName by remember {
        mutableStateOf("")
    }

    var taskTime by remember {
        mutableStateOf("")
    }


//    val timePickerState = rememberTimePickerState(
//        initialHour = 0,
//        initialMinute = 0,
//        is24Hour = true
//    )

    var taskStartHour by remember {
        mutableStateOf("")
    }

    var taskStartMin by remember {
        mutableStateOf("")
    }


    val checked = remember {
        mutableStateListOf(false, false, false, false, false, false, false)
    }

    val day = listOf("일", "월", "화", "수", "목", "금", "토")
    val daylist = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    val fixedEntities: LiveData<List<FixedEntity>> = fixedRepository.getAll()
    // Observe the LiveData using remember and collectAsState to get the latest data
    val fixedEntitiesState: List<FixedEntity> by fixedEntities.observeAsState(emptyList())

    val fixedTaskArray = Array(24){ Array(7) { "" } }

    val dayMap = mapOf(
        "Sun" to 0,
        "Mon" to 1,
        "Tue" to 2,
        "Wed" to 3,
        "Thu" to 4,
        "Fri" to 5,
        "Sat" to 6
    )

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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "매주 고정된 스케줄 입력", fontSize = 30.sp,
            modifier = Modifier.padding(bottom = 15.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 15.dp)
        ) {
            Text("일정 명")
            Spacer(modifier = Modifier.size(30.dp))
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("일정 명 입력") },
                modifier = Modifier
                //keyboardActions = KeyboardActions()
            )
        }

        //TimeInput(state = timePickerState)//추후 dropdownmenu로 수정하는 편이 좋을듯?
        //TimeInput(state = timePickerState)//추후 dropdownmenu로 수정하는 편이 좋을듯?

        //그냥 시작 시각하고 종료시각 받는 게 더 나을듯? <- 그럴거면 list에 추가하는 처리도 바꾸고 state도 따로 써야지 아무것도 안하고 그냥 시작시간에 쓰는거 그대로 복붙하면 어캄
        TimeInput(
            value1 = taskStartHour,
            onValuechange1 = {
                if (it.length <= 2)
                    taskStartHour = it
            },
            label1 = "시",
            value2 = taskStartMin,
            onValuechange2 = { taskStartMin = it },
            label2 = "분"
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 15.dp)
        ) {
            Text("소요 시간")
            Spacer(modifier = Modifier.size(30.dp))
            OutlinedTextField(
                value = taskTime,
                onValueChange = { taskTime = it },
                label = { Text(text = "소요 시간 입력(n시간)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            for (i: Int in 0..6) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = day[i])
                    Checkbox(checked = checked[i], onCheckedChange = { checked[i] = it })
                }
            }
        }

        Button(onClick = {
            if(taskName == "" || taskStartHour == "" || taskTime == ""){
                Toast.makeText(context, "입력되지 않은 항목이 있습니다", Toast.LENGTH_SHORT).show()
            }
            else if (taskStartHour.toInt() in 1..24 && taskTime.toInt() in 1..24) {

                var conflict = false

                for (i in 0..6){
                    for (j:Int in taskStartHour.toInt()-1..<taskStartHour.toInt()+taskTime.toInt()-1){
                        if (j < 24){
                            if(fixedTaskArray[j][i] != "") conflict = true
                        }else{
                            if (i == 6){
                                if(fixedTaskArray[j-24][0] != "") conflict = true
                            }else{
                                if(fixedTaskArray[j-24][i+1] != "") conflict = true
                            }
                        }
                    }
                }


                if (conflict){
                    Toast.makeText(context, "입력한 시간에 이미 스케줄이 존재합니다", Toast.LENGTH_SHORT).show()
                }else{
                    for (i: Int in 0..6) {

                        if (checked[i]  && taskTime.toInt() > 0) {
                            //val fixedTask = FixedTask(taskName, navViewModel.fixedtasklist.size, daylist[i], taskStartHour.toInt()*60+taskStartMin.toInt(), taskTime.toInt())
                            val fixedTask = FixedTask(
                                taskName,
                                navViewModel.fixedtasklist.size,
                                daylist[i],
                                taskStartHour.toInt(),
                                taskTime.toInt()
                            )
                            navViewModel.fixedtasklist.add(fixedTask)

//                            for (j:Int in taskStartHour.toInt()-1..<taskStartHour.toInt()+taskTime.toInt()-1){
//                                if (j < 24){
//                                    navViewModel.fixedTaskArray[j][i] = taskName
//                                }else {
//                                    if (i == 6) {
//                                        navViewModel.fixedTaskArray[j-24][0] = taskName
//                                    } else {
//                                        navViewModel.fixedTaskArray[j-24][i+1] = taskName
//                                    }
//                                }
//                            }
//                            val fixed = FixedEntity(
//                                name = taskName,
//                                startH = taskStartHour.toInt(),
//                                days = listOf(daylist[i]),
//                                duration = taskTime.toInt()
//                            )
                            GlobalScope.launch(Dispatchers.IO) {
                                val existing = fixedRepository.getEntityById(1)
                                if (existing != null) {
                                    existing.name = taskName
                                    existing.startH = taskStartHour.toInt()
                                    existing.days = daylist[i]
                                    existing.duration = taskTime.toInt()
                                    fixedRepository.update(existing)
                                }
                                else{
                                    var fixed = FixedEntity(taskName, taskStartHour.toInt(), daylist[i], taskTime.toInt())
                                    GlobalScope.launch(Dispatchers.IO) {
                                    fixedRepository.insert(fixed)
                                }
                                }
                            }
//                            var fixed = FixedEntity(taskName, taskStartHour.toInt(), daylist[i], taskTime.toInt())
//                            GlobalScope.launch(Dispatchers.IO) {
//                                fixedRepository.update(existing)
//                            }

                            Log.w("fixedTaskList", "${navViewModel.fixedtasklist}")
                        }
                    }
                    Log.w("array", "${navViewModel.fixedTaskArray}")
                    navController.navigate(Routes.Fixed.route)
                }
            }else{
                Toast.makeText(context, "시작시간은 1시부터 24시까지 입니다", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "추가하기")
        }
    }
}

@Composable
fun TimeInput(
    value1: String,
    onValuechange1: (String) -> Unit,
    label1: String,
    value2: String,
    onValuechange2: (String) -> Unit,
    label2: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 15.dp)
    ) {
        Text(text = "시작 시간")
        Spacer(modifier = Modifier.width(30.dp))
        OutlinedTextField(
            value = value1,
            onValueChange = onValuechange1,
            modifier = modifier
                .padding(8.dp)
                .height(56.dp)
                .width(60.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Text(label1)
//        Spacer(modifier = Modifier.width(8.dp))
//        OutlinedTextField(
//            value = value2,
//            onValueChange = onValuechange2,
//            modifier = modifier
//                .padding(8.dp)
//                .height(56.dp)
//                .width(60.dp)
//        )
//        Text(label2)
    }

}
