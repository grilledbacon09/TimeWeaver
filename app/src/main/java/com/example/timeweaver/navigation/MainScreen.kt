package com.example.timeweaver.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timeweaver.screens.AddFixedTask
import com.example.timeweaver.screens.CalendarPlus
import com.example.timeweaver.screens.CalendarScreen
import com.example.timeweaver.screens.FixedScreen
import com.example.timeweaver.screens.MyScreen
import com.example.timeweaver.screens.ScheduleScreen

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

        // 초기화 블록 실행 후 데이터베이스 내용을 로그로 출력
        LaunchedEffect(Unit) {
            //navViewModel.loadTasksFromDatabase()
//            navViewModel.logDatabaseContents()
//            navViewModel.logTaskListContents()
        }

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

//                    composable(route = Routes.CalendarPlus.route) {
//                        CalendarPlus(navController = navController)
//                    }
                    composable(route = Routes.CalendarPlus.route,
                        arguments = listOf(
                            navArgument("month") { type = NavType.StringType },
                            navArgument("day") { type = NavType.StringType },
                            navArgument("date") { type = NavType.StringType }
                        )
                        ) { backStackEntry ->
                        val month = backStackEntry.arguments?.getString("month") ?: ""
                        val day = backStackEntry.arguments?.getString("day") ?: ""
                        val date = backStackEntry.arguments?.getString("date") ?: ""
                        CalendarPlus(navController = navController,month=month,day=day, date = date)
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

                    composable(route = Routes.AddFixedTask.route){
                        AddFixedTask(navController = navController)
                    }
                }
            }
        }
    }


}