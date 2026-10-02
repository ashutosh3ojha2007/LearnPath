package com.edustudycraft.newdemoappl.presentation.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.edustudycraft.newdemoappl.domain.model.Course
import com.edustudycraft.newdemoappl.domain.model.Lesson
import com.edustudycraft.newdemoappl.presentation.LocalViewModelFactory
import com.edustudycraft.newdemoappl.presentation.common.CourseProgressBar
import com.edustudycraft.newdemoappl.presentation.common.InfoBanner
import com.edustudycraft.newdemoappl.ui.theme.NewDemoAppLTheme

@Composable
fun DashboardScreen(
    onCourseClick: (Int) -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = LocalViewModelFactory.current),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                DashboardEvent.LoggedOut -> onLoggedOut()
            }
        }
    }

    DashboardContent(
        state = state,
        onRefresh = viewModel::refresh,
        onLogout = viewModel::logout,
        onCourseClick = onCourseClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    state: DashboardUiState,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    onCourseClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isRefreshing = state is DashboardUiState.Success && state.isRefreshing
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("My Courses") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Refresh courses")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log out")
                    }
                },
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (state) {
                DashboardUiState.Loading -> CenterMessage(
                    title = "Loading courses",
                    showProgress = true,
                )

                is DashboardUiState.Error -> CenterMessage(
                    title = "Couldn't load courses",
                    body = state.message,
                    actionLabel = "Try again",
                    onAction = onRefresh,
                )

                DashboardUiState.Empty -> CenterMessage(
                    title = "No courses yet",
                    body = "When courses are assigned to you, they will show up here.",
                    actionLabel = "Refresh",
                    onAction = onRefresh,
                )

                is DashboardUiState.Success -> CourseList(
                    state = state,
                    onCourseClick = onCourseClick,
                )
            }
        }
    }
}

@Composable
private fun CourseList(
    state: DashboardUiState.Success,
    onCourseClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Pick up where you left off",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (state.isOffline) {
            item {
                InfoBanner(
                    message = "You're offline. Showing courses saved on this device.",
                    icon = Icons.Outlined.CloudOff,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
        } else if (state.message != null) {
            item {
                InfoBanner(
                    message = state.message,
                    icon = Icons.Outlined.ErrorOutline,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
        items(state.courses, key = { it.id }) { course ->
            CourseCard(
                course = course,
                onContinue = { onCourseClick(course.id) },
            )
        }
    }
}

@Composable
private fun CourseCard(
    course: Course,
    onContinue: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onContinue),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = course.title, style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = course.instructor,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            CourseProgressBar(percent = course.progressPercent, label = "Progress")
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${course.lessonCount} lessons",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 6.dp),
                )
                Spacer(modifier = Modifier.weight(1f))
                FilledTonalButton(onClick = onContinue) {
                    Text("Continue")
                }
            }
        }
    }
}

@Composable
private fun CenterMessage(
    title: String,
    body: String? = null,
    showProgress: Boolean = false,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (showProgress) {
                CircularProgressIndicator()
            }
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            if (body != null) {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (actionLabel != null) {
                Button(onClick = onAction) {
                    Text(actionLabel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() {
    NewDemoAppLTheme {
        DashboardContent(
            state = DashboardUiState.Success(
                courses = listOf(previewCourse()),
                isOffline = false,
                isRefreshing = false,
            ),
            onRefresh = {},
            onLogout = {},
            onCourseClick = {},
        )
    }
}

private fun previewCourse(): Course {
    val lessons = List(20) { index ->
        Lesson(id = index + 1, title = "Lesson ${index + 1}", isCompleted = index < 13)
    }
    return Course(
        id = 1,
        title = "Python Programming",
        instructor = "John Smith",
        lessons = lessons,
    )
}
