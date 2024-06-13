package com.example.timeweaver.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoEntity (
    var name: String,
    var timeH:Int,
    var once:Boolean,
    var importance:Int,
    @PrimaryKey(autoGenerate = true)
    var id: Int=0

)

