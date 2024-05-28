package com.example.timeweaver.navigation


sealed class Routes (val route: String) {
    object Calendar : Routes("Calendar")
    object CalendarPlus : Routes("CalendarPlus")
    object Fixed : Routes("Fixed")
    object Schedule : Routes("Schedule")
    object My : Routes("My")
    //Add Task Screen
    //Add Fixed Task Screen
    //Settings Screen
}