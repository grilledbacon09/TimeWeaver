package com.example.timeweaver.Navigation


sealed class Routes (val route: String) {
    object Calendar : Routes("Calendar")
    object Fixed : Routes("Fixed")
    object Schedule : Routes("Schedule")
    object My : Routes("My")
    //Add Task Screen
    //Add Fixed Task Screen
    //Settings Screen
}