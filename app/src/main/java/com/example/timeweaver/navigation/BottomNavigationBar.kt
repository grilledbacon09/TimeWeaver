package com.example.timeweaver.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.timeweaver.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navViewModel: NavViewModel = viewModel(viewModelStoreOwner = LocalNavGraphViewModelStoreOwner.current)

    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    if (navItem.type == 0){
                        Icon(
                            imageVector = if (currentRoute == navItem.route) navItem.onSelectedIcon else navItem.selectIcon,
                            contentDescription = navItem.title
                        )
                    }
                    else if (navItem.type == 1){
                        if (currentRoute == navItem.route)
                            Icon(painter = painterResource(id = R.drawable.fixedselected), "")
                        else
                            Icon(painter = painterResource(id = R.drawable.fixedunselected), "")
                    }
                    else if (navItem.type == 2){
                        if (currentRoute == navItem.route)
                            Icon(painter = painterResource(id = R.drawable.scheduleselected), "")
                        else
                            Icon(painter = painterResource(id = R.drawable.scheduleunselected), "")
                    }
                },
                label = {
                    Text(text = navItem.title)
                }
            )
        }
    }
}

