package com.yoiberdev.uberclone.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object LoginCliente : Screen("login_cliente")
    object LoginTaxista : Screen("login_taxista")
}
