package com.edustudycraft.newdemoappl.data.local

import androidx.room.Embedded
import androidx.room.Relation
import com.edustudycraft.newdemoappl.data.local.entity.CourseEntity
import com.edustudycraft.newdemoappl.data.local.entity.LessonEntity

data class CourseWithLessons(
    @Embedded val course: CourseEntity,
    @Relation(parentColumn = "id", entityColumn = "courseId")
    val lessons: List<LessonEntity>,
)
