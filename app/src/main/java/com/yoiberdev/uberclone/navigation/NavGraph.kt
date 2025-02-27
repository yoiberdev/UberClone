package com.yoiberdev.uberclone.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.yoiberdev.uberclone.domain.repository.AuthRepositoryImpl
import com.yoiberdev.uberclone.domain.usecase.LoginWithEmailUseCase
import com.yoiberdev.uberclone.domain.usecase.LoginWithGoogleUseCase
import com.yoiberdev.uberclone.presentation.auth.LoginScreen
import com.yoiberdev.uberclone.presentation.auth.LoginViewModel
import com.yoiberdev.uberclone.ui.screens.WelcomeScreen

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome", modifier = modifier) {
        composable("welcome") {
            WelcomeScreen (
                onClienteLogin = { navController.navigate("login_cliente") },
                onTaxistaLogin = { navController.navigate("login_taxista") }
            )
        }
        composable("login_cliente") {
            // Instanciar manualmente las dependencias
            val auth = FirebaseAuth.getInstance()
            val authRepository = AuthRepositoryImpl(auth)
            val loginWithEmailUseCase = LoginWithEmailUseCase(authRepository)
            val loginWithGoogleUseCase = LoginWithGoogleUseCase(authRepository)
            val viewModel = LoginViewModel(loginWithEmailUseCase, loginWithGoogleUseCase)

            LoginScreen (
                role = "Cliente",
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    // Acción post login para Cliente
                }
            )
        }
        composable("login_taxista") {
            // Instanciar manualmente las dependencias
            val auth = FirebaseAuth.getInstance()
            val authRepository = AuthRepositoryImpl(auth)
            val loginWithEmailUseCase = LoginWithEmailUseCase(authRepository)
            val loginWithGoogleUseCase = LoginWithGoogleUseCase(authRepository)
            val viewModel = LoginViewModel(loginWithEmailUseCase, loginWithGoogleUseCase)

            LoginScreen(
                role = "Taxista",
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    // Acción post login para Taxista
                }
            )
        }
    }
}
