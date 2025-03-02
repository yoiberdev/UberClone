package com.yoiberdev.uberclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.yoiberdev.uberclone.domain.usecase.LoginWithEmailUseCase
import com.yoiberdev.uberclone.domain.usecase.LoginWithGoogleUseCase
import com.yoiberdev.uberclone.presentation.auth.LoginScreen
import com.yoiberdev.uberclone.presentation.auth.LoginViewModel
import com.yoiberdev.uberclone.presentation.home.HomeScreen
import com.yoiberdev.uberclone.presentation.map.MapScreen
import com.yoiberdev.uberclone.presentation.welcome.WelcomeScreen

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome", modifier = modifier) {
        composable("welcome") {
            WelcomeScreen(
                onClienteLogin = { navController.navigate("login_cliente") },
                onTaxistaLogin = { navController.navigate("login_taxista") }
            )
        }
        composable("login_cliente") {
            // Instanciar manualmente las dependencias
            val auth = FirebaseAuth.getInstance()
            val authRepository = com.yoiberdev.uberclone.domain.repository.AuthRepositoryImpl(auth)
            val loginWithEmailUseCase = LoginWithEmailUseCase(authRepository)
            val loginWithGoogleUseCase = LoginWithGoogleUseCase(authRepository)
            val viewModel = LoginViewModel(loginWithEmailUseCase, loginWithGoogleUseCase)

            LoginScreen(
                role = "Cliente",
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    // Una vez logueado, navega a HomeScreen
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
        composable("login_taxista") {
            // Instanciar manualmente las dependencias
            val auth = FirebaseAuth.getInstance()
            val authRepository = com.yoiberdev.uberclone.domain.repository.AuthRepositoryImpl(auth)
            val loginWithEmailUseCase = LoginWithEmailUseCase(authRepository)
            val loginWithGoogleUseCase = LoginWithGoogleUseCase(authRepository)
            val viewModel = LoginViewModel(loginWithEmailUseCase, loginWithGoogleUseCase)

            LoginScreen(
                role = "Taxista",
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    // Una vez logueado, navega a HomeScreen
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onLogout = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onChat = { navController.navigate(Screen.Chat.route) },
                onMap = { navController.navigate(Screen.Map.route) }
            )
        }

        composable(Screen.Map.route) {
            MapScreen(onBack = { navController.popBackStack() })
        }
    }
}
