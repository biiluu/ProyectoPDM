package com.example.proyectopdm.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectopdm.data.SessionManager
import com.example.proyectopdm.screens.*
import com.example.proyectopdm.viewmodel.NotificationViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(onContinueClick = {
                val savedCarnet = sessionManager.getSavedCarnet()
                if (savedCarnet != null) {
                    navController.navigate("main/$savedCarnet") {
                        popUpTo("splash") { inclusive = true }
                    }
                } else {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
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
            
            // Actualizar la última actividad cada vez que se entra o se usa la pantalla principal
            sessionManager.updateLastActivity()
            
            MainScreen(
                carnet = carnet,
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate("login") {
                        popUpTo("main/$carnet") { inclusive = true }
                    }
                },
                navController = navController
            )
        }
        composable(
            route = "notifications/{carnet}",
            arguments = listOf(navArgument("carnet") { type = NavType.StringType })
        ) { backStackEntry ->
            val carnet = backStackEntry.arguments?.getString("carnet") ?: ""
            val viewModel: NotificationViewModel = viewModel()
            NotificationScreen(
                carnet = carnet,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("help") {
            HelpSupportScreen(navController = navController)
        }
        composable("about") {
            AboutScreen(navController = navController)
        }
    }
}
