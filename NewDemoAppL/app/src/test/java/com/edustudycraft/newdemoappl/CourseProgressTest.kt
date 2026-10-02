package com.edustudycraft.newdemoappl

import com.edustudycraft.newdemoappl.domain.model.CourseProgress
import com.edustudycraft.newdemoappl.domain.model.LessonMerge
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseProgressTest {
    @Test
    fun sampleCatalog_seedsLessonCountsThatMatchDisplayedProgress() {
        assertEquals(13, CourseProgress.seededCompletedCount(65, 20))
        assertEquals(65, CourseProgress.percent(13, 20))

        assertEquals(6, CourseProgress.seededCompletedCount(40, 16))
        assertEquals(37, CourseProgress.percent(6, 16))

        assertEquals(7, CourseProgress.seededCompletedCount(25, 28))
        assertEquals(25, CourseProgress.percent(7, 28))
    }

    @Test
    fun percent_handlesEmptyAndCompleteCourses() {
        assertEquals(0, CourseProgress.percent(completedCount = 0, totalCount = 0))
        assertEquals(0, CourseProgress.percent(completedCount = 0, totalCount = 12))
        assertEquals(100, CourseProgress.percent(completedCount = 12, totalCount = 12))
        assertEquals(100, CourseProgress.percent(completedCount = 20, totalCount = 12))
    }

    @Test
    fun merge_keepsLocalCompletionWhenServerSnapshotIsStillPending() {
        assertTrue(
            LessonMerge.isCompleted(
                remoteCompleted = false,
                lessonId = 114,
                locallyCompletedIds = setOf(114),
            ),
        )
        assertTrue(
            LessonMerge.isCompleted(
                remoteCompleted = true,
                lessonId = 101,
                locallyCompletedIds = emptySet(),
            ),
        )
        assertFalse(
            LessonMerge.isCompleted(
                remoteCompleted = false,
                lessonId = 114,
                locallyCompletedIds = emptySet(),
            ),
        )
    }
}
