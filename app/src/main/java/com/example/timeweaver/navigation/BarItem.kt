package com.example.timeweaver.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.ui.graphics.vector.ImageVector

data class BarItem (val title: String, val selectIcon: ImageVector, val onSelectedIcon: ImageVector, val route: String, val type: Int)

object NavBarItems{

    val BarItems = listOf(
        BarItem(
            title = "Calendar",
            selectIcon = Icons.Default.DateRange,
            onSelectedIcon = Icons.Outlined.DateRange,
            route = "Calendar",
            type = 0
        ),
        BarItem(
            title = "Fixed",
            selectIcon = Icons.Default.ArrowForward,
            onSelectedIcon = Icons.Default.ArrowForward,
            route = "Fixed",
            type = 1
        ),
        BarItem(
            title = "Schedule",
            selectIcon = Icons.Default.ArrowBack,
            onSelectedIcon = Icons.Default.ArrowBack,
            route = "Schedule",
            type = 2
        ),
        BarItem(
            title = "My",
            selectIcon = Icons.Default.FavoriteBorder,
            onSelectedIcon = Icons.Default.Favorite,
            route = "My",
            type = 0
        )
    )
}