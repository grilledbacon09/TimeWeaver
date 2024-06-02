package com.example.timeweaver.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.timeweaver.navigation.LocalNavGraphViewModelStoreOwner
import com.example.timeweaver.navigation.NavViewModel
import com.example.timeweaver.navigation.Routes

@Composable
fun FixedScreen(navController: NavHostController) {
    val navViewModel: NavViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)
    val scrollState = rememberScrollState()
    val day = listOf("일", "월", "화", "수", "목", "금", "토")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Fixed Task Screen")
        Box{
            Row(modifier = Modifier.verticalScroll(scrollState)) {
                for (column in 0 until 8){
                    Column {
                        for (row in 0 until 25){
                            Box {
                                if (column == 0 && row == 0)
                                    TableCell(text = "")
                                else if (row == 0)
                                    TableCell(text = "${day[column-1]}요일")
                                else if (column == 0)
                                    TableCell(text = "${row}시")
                                else
                                    TableCell(text = "${day[column-1]}요일 ${row}시")

                            }
                        }
                    }
                }
            }
            ListPlusButton {
                navController.navigate(Routes.AddFixedTask.route)
            }
        }

        //Log.w("fixedList", "${navViewModel.fixedtasklist}")
    }
}

@Composable
fun TableCell(text: String) {
    Box(
        modifier = Modifier
            .border(1.dp, Color.Black)
            .padding(3.dp)
            .width(50.dp)
            .height(50.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}