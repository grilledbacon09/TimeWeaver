package com.example.timeweaver.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FixedEntity::class], version = 1)
abstract class FixedDatabase : RoomDatabase() {
    abstract fun fixedDao(): FixedDAO

    companion object {
        private var database:FixedDatabase?=null
        fun getFixedDatabase(context: Context): FixedDatabase {
            return database ?: Room.databaseBuilder(
                context,
                FixedDatabase::class.java,
                "Fixed"
            ).build()
                .also {
                    database = it
                }
        }
    }
}
