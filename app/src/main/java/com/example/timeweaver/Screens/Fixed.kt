package com.example.timeweaver.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.timeweaver.Navigation.LocalNavGraphViewModelStoreOwner
import com.example.timeweaver.Navigation.NavViewModel

@Composable
fun FixedScreen(navController: NavHostController) {
    val navViewModel: NavViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    Column {
        Text(navViewModel.tasklist[1].name)
    }
}