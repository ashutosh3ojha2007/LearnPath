package com.edustudycraft.newdemoappl.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.edustudycraft.newdemoappl.data.local.entity.CourseEntity
import com.edustudycraft.newdemoappl.data.local.entity.LessonEntity

@Dao
abstract class CourseDao {
    @Transaction
    @Query("SELECT * FROM courses ORDER BY id ASC")
    abstract suspend fun getCourses(): List<CourseWithLessons>

    @Query("SELECT id FROM lessons WHERE completed = 1")
    abstract suspend fun completedLessonIds(): List<Int>

    @Query("SELECT COUNT(*) FROM courses")
    abstract suspend fun courseCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCourses(courses: List<CourseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertLessons(lessons: List<LessonEntity>)

    @Query("DELETE FROM lessons")
    abstract suspend fun clearLessons()

    @Query("DELETE FROM courses")
    abstract suspend fun clearCourses()

    @Query("UPDATE lessons SET completed = 1 WHERE id = :lessonId")
    abstract suspend fun markLessonCompleted(lessonId: Int)

    @Transaction
    open suspend fun replaceCatalog(
        courses: List<CourseEntity>,
        lessons: List<LessonEntity>,
    ) {
        clearLessons()
        clearCourses()
        if (courses.isNotEmpty()) insertCourses(courses)
        if (lessons.isNotEmpty()) insertLessons(lessons)
    }
}
