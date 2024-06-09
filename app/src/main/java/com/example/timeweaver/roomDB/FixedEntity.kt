package com.example.timeweaver.roomDB

import androidx.room.PrimaryKey

class FixedEntity {
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
    var name: String?=""
    var startH:Int? = 0
    var startM:Int? = 0
    var duration:Int? = 0
    var days = BooleanArray(7)
}