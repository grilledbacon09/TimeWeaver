package com.example.timeweaver.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoEntity (
    var name: String,
    @PrimaryKey(autoGenerate = true)
    var id: Int=0,
    var importance:Int,
    var completed:Boolean,
    var once:Boolean,
    var deadline:Int,
    var timeH:Int

)

