package com.example.timeweaver.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FixedEntity::class], version = 1)
abstract class FixedDatabase : RoomDatabase() {
    abstract fun fixedDao(): FixedDAO

    companion object {
        @Volatile
        private var instance: FixedDatabase? = null

        fun getInstance(context: Context): FixedDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                FixedDatabase::class.java, "FixedDB"
            ).build()
    }
}
