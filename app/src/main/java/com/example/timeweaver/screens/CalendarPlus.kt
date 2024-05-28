package com.example.timeweaver.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.timeweaver.navigation.LocalNavGraphViewModelStoreOwner
import com.example.timeweaver.navigation.NavViewModel
import com.example.timeweaver.roomDB.TodoDatabase
import com.example.timeweaver.roomDB.TodoEntity

@Composable
fun CalendarPlus(navController: NavHostController,selectedEntity: TodoEntity?=null) {

    val navViewModel: NavViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    val context = LocalContext.current
    val itemdb = TodoDatabase.getItemDatabase(context);
//    val viewModel:ItemViewModel =
//        viewModel(factory =ItemViewModelFactory(Repository(itemdb)) )//Factory 객체

    var itemId by remember {
        mutableStateOf("")
    }
    var scheduleName by navViewModel.scheduleName//string
    var estimatedTimeH by navViewModel.estimatedTimeH//string
    var estimatedTimeM by navViewModel.estimatedTimeM//string
    var once by navViewModel.once//boolean
    var importance by navViewModel.importance//string

    val id = itemId.toIntOrNull()?:0
    val timeH = estimatedTimeH.toIntOrNull() ?:0
    val timeM = estimatedTimeH.toIntOrNull()?:0
    val importance1 = importance.toIntOrNull()?:0

    val Entity = TodoEntity(id,scheduleName,timeH,timeM,once,importance1)

    LaunchedEffect(selectedEntity) {
        if (selectedEntity != null) {
            var id = selectedEntity.id.toString()
            var name= selectedEntity.name
            var timeH= selectedEntity.timeH.toString()
            var timeM= selectedEntity.timeM.toString()
            var once= selectedEntity.once
            var importance= selectedEntity.importance.toString()
        }
    }

    fun clearText(){//다 입력하고 버튼 누르면 비우고 싶다
        var id = ""
        var name= ""
        var timeH= ""
        var timeM= ""
        var once= ""
        var importance= ""
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = "5월 27일 해야할 일",
            modifier = Modifier.padding(bottom = 20.dp),
            style = TextStyle(
                fontSize = 20.sp, // 텍스트 크기 설정
                fontWeight = FontWeight.Bold // 볼드체 스타일 설정
            )
        )
        EditNameField(value = scheduleName, onValuechange ={scheduleName = it} , label = "일정 명",
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp))
        EditTimeField(
            value1 = estimatedTimeH,
            onValuechange1 = {estimatedTimeH=it},
            label1 ="시간" ,
            value2 = estimatedTimeM ,
            onValuechange2 = {estimatedTimeM=it},
            label2 ="분"
        )
        OnceCheckBox(
            checked = once,
            onCheckedChange = navViewModel.changeTaskChecked()
        )
        ImportanceField(value = importance, onValuechange = {importance=it})

    }

}

@Composable
fun EditNameField(value:String,
                  onValuechange:(String)->Unit,
                  label:String,
                  modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 15.dp)) {
        Text("일정 명")
        Spacer(modifier = Modifier.size(30.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValuechange,
            label= {Text(label)},
            modifier=Modifier
        )
    }


}

@Composable
fun EditTimeField(value1:String,
                  onValuechange1:(String)->Unit,
                  label1:String,
                  value2:String,
                  onValuechange2:(String)->Unit,
                  label2:String,
                  modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 15.dp)) {
        Text(text = "소요시간")
        Spacer(modifier = Modifier.width(30.dp))
        OutlinedTextField(
            value = value1,
            onValueChange = onValuechange1,
            modifier = modifier
                .padding(8.dp)
                .height(56.dp)
                .width(60.dp)
        )
        Text(label1)
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = value2,
            onValueChange = onValuechange2,
            modifier = modifier
                .padding(8.dp)
                .height(56.dp)
                .width(60.dp)
        )
        Text(label2)
    }

}

@Composable
fun OnceCheckBox(
    checked:Boolean,
    onCheckedChange: Unit,
    modifier: Modifier = Modifier,
    viewModel: NavViewModel = viewModel()
) {
    Row (modifier=Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically){
        Text(text = "한번에 끝내야하는 일인가요?")
        Spacer(modifier = Modifier.width(30.dp))
        Checkbox(checked = checked, onCheckedChange = { onCheckedChange })


    }
}

@Composable
fun ImportanceField(value:String,
                    onValuechange:(String)->Unit,
                    modifier: Modifier = Modifier) {
    Row (verticalAlignment = Alignment.CenterVertically){
        Text(text = "중요도")
        Spacer(modifier = Modifier.width(30.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValuechange,
            modifier=Modifier
        )

    }

}