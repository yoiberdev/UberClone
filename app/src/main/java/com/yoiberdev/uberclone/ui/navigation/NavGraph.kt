package com.yoiberdev.uberclone.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yoiberdev.uberclone.ui.screens.WelcomeScreen
import com.yoiberdev.uberclone.ui.screens.LoginScreen

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
            LoginScreen(
                role = "Cliente",
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    // Acción post login para Cliente
                }
            )
        }
        composable("login_taxista") {
            LoginScreen(
                role = "Taxista",
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    // Acción post login para Taxista
                }
            )
        }
    }
}
