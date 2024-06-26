package com.example.timeweaver.navigation


sealed class Routes (val route: String) {
    object Calendar : Routes("Calendar")
    //object CalendarPlus : Routes("CalendarPlus")
    object CalendarPlus : Routes("CalendarPlus/{month}/{day}/{date}") {
        fun createRoute(month: String, day: String, date: String) = "CalendarPlus/$month/$day/$date"
    }
    object Fixed : Routes("Fixed")
    object Schedule : Routes("Schedule")
    object My : Routes("My")
    object AddFixedTask : Routes("AddFixedTask")
    //Add Task Screen
    //Add Fixed Task Screen
    //Settings Screen
}