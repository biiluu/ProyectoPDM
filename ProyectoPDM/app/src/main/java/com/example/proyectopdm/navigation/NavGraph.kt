package com.example.proyectopdm.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectopdm.screens.LoginScreen
import com.example.proyectopdm.screens.MainScreen
import com.example.proyectopdm.screens.SplashScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(onContinueClick = {
                navController.navigate("login")
            })
        }
        composable("login") {
            LoginScreen(onLoginSuccess = { carnet ->
                navController.navigate("main/$carnet") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable(
            route = "main/{carnet}",
            arguments = listOf(navArgument("carnet") { type = NavType.StringType })
        ) { backStackEntry ->
            val carnet = backStackEntry.arguments?.getString("carnet") ?: ""
            MainScreen(
                carnet = carnet,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("main/$carnet") { inclusive = true }
                    }
                }
            )
        }
    }
}