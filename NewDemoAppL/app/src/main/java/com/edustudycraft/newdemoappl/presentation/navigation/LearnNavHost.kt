package com.edustudycraft.newdemoappl.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.edustudycraft.newdemoappl.presentation.dashboard.DashboardScreen
import com.edustudycraft.newdemoappl.presentation.detail.CourseDetailScreen
import com.edustudycraft.newdemoappl.presentation.login.LoginScreen

@Composable
fun LearnNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onCourseClick = { courseId ->
                    navController.navigate(Routes.courseDetail(courseId))
                },
                onLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
            )
        }
        composable(
            route = Routes.COURSE_DETAIL,
            arguments = listOf(navArgument(Routes.COURSE_ID) { type = NavType.IntType }),
        ) {
            CourseDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
