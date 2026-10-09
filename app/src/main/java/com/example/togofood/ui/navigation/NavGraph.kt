package com.example.togofood.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.togofood.ui.screens.auth.LoginScreen
import com.example.togofood.ui.screens.auth.RegisterScreen
import com.example.togofood.ui.screens.main.MainScreen
import com.example.togofood.ui.theme.TogoMotion
import com.example.togofood.ui.theme.togoDetailEnter
import com.example.togofood.ui.theme.togoDetailExit
import com.example.togofood.ui.theme.togoDetailPopEnter
import com.example.togofood.ui.theme.togoDetailPopExit

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.MAIN,
        enterTransition = { togoDetailEnter() },
        exitTransition = { togoDetailExit() },
        popEnterTransition = { togoDetailPopEnter() },
        popExitTransition = { togoDetailPopExit() }
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToMain = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToMain = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            Routes.MAIN,
            enterTransition = { fadeIn(TogoMotion.tweenSlow()) },
            exitTransition = { fadeOut(TogoMotion.tweenFast()) }
        ) {
            MainScreen(
                onLoginClick = { navController.navigate(Routes.LOGIN) }
            )
        }
    }
}
