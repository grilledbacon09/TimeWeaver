package com.example.timeweaver.screens

data class Task(val name: String, val ID: Int, val importance: Int, var completed: Boolean = false, val once: Boolean, var deadline: Int, var time: Int)
//name: 이름
//ID: 모든 할 일 구분하기 위함
//importance: 중요도
//completed: 끝난 일인지 확인
//once: 한 번에 끝내야 하는 일인지
//deadline: 일의 deadline까지 남은 일 수 -> 끝났으면 0
//time: 남은 수행 시간
