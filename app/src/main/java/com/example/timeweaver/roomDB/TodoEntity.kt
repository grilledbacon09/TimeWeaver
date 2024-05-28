package com.example.timeweaver.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    var name: String?="",
    var timeH:Int?,
    var timeM:Int?,
    var once:Boolean,
    var importance:Int?

)