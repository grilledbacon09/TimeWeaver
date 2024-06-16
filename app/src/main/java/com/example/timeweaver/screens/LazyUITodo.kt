package com.example.timeweaver.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timeweaver.roomDB.TodoEntity

@Composable
fun TodoItemList(
    list: List<TodoEntity>,
    onEntityClick: (TodoEntity) -> Unit
    //onClearClick: () -> Unit
) {
    LazyColumn {
        items(list) { item ->
            TodoItemUI(item, onEntityClick)
            Divider(color = Color.Black, thickness = 1.dp)
        }
    }
//    Button(
//        onClick = onClearClick,
//        modifier = Modifier.padding(16.dp)
//    ) {
//        Text("Clear")
//    }
}

@Composable
fun TodoItemUI(item: TodoEntity, onItemClick: (item:TodoEntity) -> Unit) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable { onItemClick(item) }
    ) {
        Text(item.id.toString(), fontSize = 15.sp)
        Text(item.name.toString(), fontSize = 15.sp)
        Text(item.timeH.toString(), fontSize = 15.sp)
        Text(item.once.toString(), fontSize = 15.sp)
        Text(item.importance.toString(), fontSize = 15.sp)
    }
}