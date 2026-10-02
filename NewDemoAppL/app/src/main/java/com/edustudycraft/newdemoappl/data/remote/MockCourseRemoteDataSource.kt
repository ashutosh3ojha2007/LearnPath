package com.edustudycraft.newdemoappl.data.remote

import android.content.Context
import com.edustudycraft.newdemoappl.data.network.NetworkMonitor
import com.edustudycraft.newdemoappl.data.remote.dto.CourseDto
import kotlinx.coroutines.delay
import org.json.JSONArray
import java.io.IOException

/**
 * Reads [ASSET_NAME] after a short delay when the device is online.
 * This is the seam a Retrofit implementation would replace.
 */
class MockCourseRemoteDataSource(
    private val context: Context,
    private val networkMonitor: NetworkMonitor,
) : CourseRemoteDataSource {

    override suspend fun fetchCourses(): List<CourseDto> {
        if (!networkMonitor.isCurrentlyOnline()) {
            throw IOException(OFFLINE_MESSAGE)
        }
        delay(NETWORK_DELAY_MS)
        if (!networkMonitor.isCurrentlyOnline()) {
            throw IOException(OFFLINE_MESSAGE)
        }
        val json = context.assets.open(ASSET_NAME).bufferedReader().use { it.readText() }
        return parse(json)
    }

    private fun parse(json: String): List<CourseDto> {
        val array = JSONArray(json)
        return List(array.length()) { index ->
            val item = array.getJSONObject(index)
            CourseDto(
                id = item.getInt("id"),
                title = item.getString("title"),
                instructor = item.getString("instructor"),
                progress = item.getInt("progress"),
                lessonCount = item.getInt("lessons"),
            )
        }
    }

    private companion object {
        const val ASSET_NAME = "courses.json"
        const val NETWORK_DELAY_MS = 800L
        const val OFFLINE_MESSAGE = "No internet connection. Connect to the internet and try again."
    }
}
