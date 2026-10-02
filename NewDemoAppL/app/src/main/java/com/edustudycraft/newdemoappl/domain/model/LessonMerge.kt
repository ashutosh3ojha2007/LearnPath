package com.edustudycraft.newdemoappl.domain.model

/**
 * A later server snapshot must not undo a lesson the learner already finished
 * on this device. Completion is one-way until a real sync engine exists.
 */
object LessonMerge {
    fun isCompleted(
        remoteCompleted: Boolean,
        lessonId: Int,
        locallyCompletedIds: Set<Int>,
    ): Boolean = remoteCompleted || lessonId in locallyCompletedIds
}
