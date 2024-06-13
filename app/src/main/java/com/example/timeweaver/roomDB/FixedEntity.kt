package com.example.timeweaver.roomDB
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FixedEntity")
data class FixedEntity(
    var name: String,
    var startH: Int,
    var days: String,
    var duration: Int,
    @PrimaryKey(autoGenerate = true)
    val itemId:Int=0
)
