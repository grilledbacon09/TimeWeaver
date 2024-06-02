package com.example.timeweaver.screens

data class FixedTask(
    val name: String = "",
    val ID: Int = 0,
    val day: String = "",
    val startTime: Int = 0,
    val length: Int = 0
)
//day: 일 ~ 토 중 어느 날 수행하는지
//startTime: 몇 시에 시작한는지
//length: 몇 분 동안 일이 수행되는지
