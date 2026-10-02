package com.edustudycraft.newdemoappl.domain.model

/**
 * Progress is always derived from lesson completion so the dashboard and the
 * detail screen cannot show different numbers for the same course.
 */
object CourseProgress {
    fun percent(completedCount: Int, totalCount: Int): Int {
        if (totalCount <= 0 || completedCount <= 0) return 0
        val completed = completedCount.coerceAtMost(totalCount)
        return (completed * 100) / totalCount
    }

    /**
     * Maps a server progress value onto a whole number of completed lessons.
     * Integer division is intentional: 40% of 16 lessons is 6, which displays as 37%.
     */
    fun seededCompletedCount(progressPercent: Int, totalCount: Int): Int {
        if (totalCount <= 0 || progressPercent <= 0) return 0
        val progress = progressPercent.coerceIn(0, 100)
        return ((progress * totalCount) / 100).coerceAtMost(totalCount)
    }
}
