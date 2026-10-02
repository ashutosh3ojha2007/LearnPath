package com.edustudycraft.newdemoappl.presentation.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edustudycraft.newdemoappl.domain.model.Lesson
import com.edustudycraft.newdemoappl.presentation.LocalViewModelFactory
import com.edustudycraft.newdemoappl.presentation.common.CourseProgressBar
import com.edustudycraft.newdemoappl.presentation.common.InfoBanner

@Composable
fun CourseDetailScreen(
    onBack: () -> Unit,
    viewModel: CourseDetailViewModel = viewModel(factory = LocalViewModelFactory.current),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CourseDetailContent(
        state = state,
        onBack = onBack,
        onMarkCompleted = viewModel::markLessonCompleted,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailContent(
    state: CourseDetailUiState,
    onBack: () -> Unit,
    onMarkCompleted: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = (state as? CourseDetailUiState.Success)?.course?.title ?: "Course"
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        when (state) {
            CourseDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is CourseDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = state.message, style = MaterialTheme.typography.bodyLarge)
                }
            }

            is CourseDetailUiState.Success -> {
                CourseDetailBody(
                    state = state,
                    onMarkCompleted = onMarkCompleted,
                    contentPadding = innerPadding,
                )
            }
        }
    }
}

@Composable
private fun CourseDetailBody(
    state: CourseDetailUiState.Success,
    onMarkCompleted: (Int) -> Unit,
    contentPadding: PaddingValues,
) {
    val course = state.course
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = course.instructor,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (state.isOffline) {
            item {
                InfoBanner(
                    message = "You're offline. Lesson updates are saved on this device.",
                    icon = Icons.Outlined.CloudOff,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CourseProgressBar(
                        percent = course.progressPercent,
                        label = "Current progress",
                    )
                    Text(
                        text = "${course.completedCount} of ${course.lessonCount} lessons completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        item {
            Text(text = "Lessons", style = MaterialTheme.typography.titleMedium)
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    course.lessons.forEachIndexed { index, lesson ->
                        LessonRow(
                            lesson = lesson,
                            onMarkCompleted = onMarkCompleted,
                        )
                        if (index < course.lessons.lastIndex) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonRow(
    lesson: Lesson,
    onMarkCompleted: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = !lesson.isCompleted,
                role = Role.Button,
                onClick = { onMarkCompleted(lesson.id) },
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = if (lesson.isCompleted) {
                Icons.Filled.CheckCircle
            } else {
                Icons.Outlined.RadioButtonUnchecked
            },
            contentDescription = null,
            tint = if (lesson.isCompleted) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.size(22.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = lesson.title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = if (lesson.isCompleted) "Completed" else "Pending",
                style = MaterialTheme.typography.bodySmall,
                color = if (lesson.isCompleted) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
        if (!lesson.isCompleted) {
            Text(
                text = "Complete",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
