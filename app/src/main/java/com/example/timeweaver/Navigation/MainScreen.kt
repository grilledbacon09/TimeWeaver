package com.example.timeweaver.Navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.timeweaver.Screens.CalendarScreen
import com.example.timeweaver.Screens.FixedScreen
import com.example.timeweaver.Screens.MyScreen
import com.example.timeweaver.Screens.ScheduleScreen

@Composable
fun rememberViewModelStoreOwner(): ViewModelStoreOwner {
    val context = LocalContext.current
    return remember(context) { context as ViewModelStoreOwner }
}

val LocalNavGraphViewModelStoreOwner =
    staticCompositionLocalOf<ViewModelStoreOwner> {
        error("Undefined")
    }

@Composable
fun Navigate(navController: NavHostController) {

    val navStoreOwner = rememberViewModelStoreOwner()

    CompositionLocalProvider(LocalNavGraphViewModelStoreOwner provides navStoreOwner) {
        val navViewModel: NavViewModel =
            viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

        Scaffold (
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        ) { contentPadding ->
            Column(modifier = Modifier.padding(contentPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = Routes.Calendar.route
                ) {

                    composable(route = Routes.Calendar.route) {
                        CalendarScreen(navController = navController)
                    }

                    composable(route = Routes.Fixed.route) {
                        FixedScreen(navController = navController)
                    }

                    composable(route = Routes.Schedule.route) {
                        ScheduleScreen(navController = navController)
                    }

                    composable(route = Routes.My.route) {
                        MyScreen(navController = navController)
                    }
                }
            }
        }
    }


}