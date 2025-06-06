package com.example.incognito.ViewModel

import InfoScreen
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.incognito.Model.Routes
import com.example.incognito.View.InfoScreenA

@Composable
fun IncognitoNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.infoScreen) {
        composable(Routes.infoScreen) {
            InfoScreen(viewModel = viewModel,
                navController = navController,
                onNavigateToGame = { playerCount ->
                    navController.navigate("${Routes.infoScreenA}/$playerCount")
                }
            )
        }

        composable(
            route = "${Routes.infoScreenA}/{playerCount}",
            arguments = listOf(navArgument("playerCount") { type = NavType.IntType })
        ) { backStackEntry ->
            val playerCount = backStackEntry.arguments?.getInt("playerCount") ?: 0
            InfoScreenA(viewModel = viewModel,
                playerCount = playerCount,
                onNavigateToStart = {
                    navController.navigate(Routes.infoScreen) {
                        popUpTo(Routes.infoScreen) { inclusive = true }
                    }
                }
            )
        }
    }
}