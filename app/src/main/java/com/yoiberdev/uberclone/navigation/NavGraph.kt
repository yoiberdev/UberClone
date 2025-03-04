package com.yoiberdev.uberclone.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.yoiberdev.uberclone.domain.usecase.LoginWithEmailUseCase
import com.yoiberdev.uberclone.domain.usecase.LoginWithGoogleUseCase
import com.yoiberdev.uberclone.presentation.auth.LoginScreen
import com.yoiberdev.uberclone.presentation.auth.LoginViewModel
import com.yoiberdev.uberclone.presentation.home.HomeScreen
import com.yoiberdev.uberclone.presentation.location.LocationScreen
import com.yoiberdev.uberclone.presentation.map.ClientMapScreen
import com.yoiberdev.uberclone.presentation.map.ClientRequestsScreen
import com.yoiberdev.uberclone.presentation.map.LocationPermissionContainer
import com.yoiberdev.uberclone.presentation.map.MapScreen
import com.yoiberdev.uberclone.presentation.map.TaxiMapDetailScreen
import com.yoiberdev.uberclone.presentation.map.TaxiMapScreen
import com.yoiberdev.uberclone.presentation.map.TaxiMapViewModel
import com.yoiberdev.uberclone.presentation.map.TaxiRequestsScreen
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
            // Instanciar las dependencias para Cliente
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
                    // Navegar a la pantalla de mapa para cliente
                    navController.navigate("client_map") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
        composable("login_taxista") {
            // Instanciar las dependencias para Taxista
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
                    navController.navigate("taxi_home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }
        composable("client_map") {
            ClientMapScreen(
                onBack = { navController.popBackStack() },
                onRequestSuccess = { navController.navigate("client_requests") }
            )
        }
        composable("client_requests") {
            ClientRequestsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("taxi_home") {
            com.yoiberdev.uberclone.presentation.home.TaxiHomeScreen(
                onLogout = {
                    navController.navigate("welcome") {
                        popUpTo("taxi_home") { inclusive = true }
                    }
                },
                onViewRequests = {
                    navController.navigate("taxi_requests")
                }
            )
        }
        composable("taxi_requests") {
            TaxiRequestsScreen(
                onBack = { navController.popBackStack() },
                onRequestSelected = { request ->
                    navController.navigate("taxi_detail/${request.id}")
                }
            )
        }
        composable("taxi_map") {
            TaxiMapScreen(onBack = { navController.popBackStack() })
        }

        composable("taxi_detail/{requestId}") { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId")
            // Obtén tu ViewModel que contiene la lista de solicitudes
            val taxiViewModel: TaxiMapViewModel = viewModel ()
            // Busca la solicitud con el requestId obtenido
            val request = taxiViewModel.rideRequests.value.find { it.id == requestId }


            if (request != null) {
                TaxiMapDetailScreen(
                    onBack = { navController.popBackStack() },
                    rideRequest = request
                )
            } else {
                // Si no se encuentra, muestra un mensaje de error o una pantalla vacía.
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Solicitud no encontrada")
                }
            }
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

        composable("map") {
            MapScreen(onBack = { navController.popBackStack() })
        }
    }
}
