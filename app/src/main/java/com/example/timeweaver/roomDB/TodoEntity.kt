package com.example.timeweaver.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.timeweaver.screens.Task

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

){
    fun toTask():  Task{
            return Task(
                name = this.name,
                ID = this.id,
                once = this.once,
                importance = this.importance,
                completed = this.completed,
                deadline = this.deadline,
                time = this.timeH
            )
        }
    }


