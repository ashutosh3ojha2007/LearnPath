package com.edustudycraft.newdemoappl.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.edustudycraft.newdemoappl.data.local.entity.CourseEntity
import com.edustudycraft.newdemoappl.data.local.entity.LessonEntity

@Database(
    entities = [CourseEntity::class, LessonEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class LearningDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao

    companion object {
        fun create(context: Context): LearningDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                LearningDatabase::class.java,
                "learning.db",
            ).fallbackToDestructiveMigration()
                .build()
        }
    }
}
