package com.example.timeweaver.screens

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
    val navViewModel: NavViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    val scrollState = rememberScrollState()
    val day = listOf("일", "월", "화", "수", "목", "금", "토")
    val daylist = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //Text(text = "Fixed Task Screen")
        Box{
            Row(modifier = Modifier.verticalScroll(scrollState)) {
                for (column in 0 until 8){
                    Column {
                        for (row in 0 until 25){
                            Box {
                                if (column == 0 && row == 0)
                                    TableCell(text = "", Color.White)
                                else if (row == 0)
                                    TableCell(text = "${day[column-1]}요일", Color.White)
                                else if (column == 0)
                                    TableCell(text = "${row}시", Color.White)
                                else {
                                    TableCell(text = "", Color.White)
                                    if (navViewModel.fixedTaskArray[row-1][column-1] != "")
                                        TableCell(text = navViewModel.fixedTaskArray[row-1][column-1], color = Color.LightGray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}