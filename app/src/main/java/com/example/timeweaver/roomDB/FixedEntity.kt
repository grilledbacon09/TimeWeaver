package com.example.timeweaver.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FixedEntity")
class FixedEntity {
    @PrimaryKey(autoGenerate = true)
    var id: String? = null,
    var name: String = "",
    var startH: Int,
    var startM:Int,
    var duration:Int,
    var daylist: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri"),
    var importance:Int
}
