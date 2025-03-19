package com.example.soundtrainer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel

//@Composable
//fun AppNavigation(viewModel: BalloonViewModel) {
//    var gameStarted by rememberSaveable { mutableStateOf(false) }
//    println("Katya gameStarted $gameStarted")
//
//    if (gameStarted) {
//        println("Katya Ballonscreen")
//        BalloonScreen(viewModel, onExit = { gameStarted = false })
//    } else {
//        println("Katya startscreen")
//
//        StartScreen(
//            onStartGame = { gameStarted = true },
//            viewModel = viewModel
//        )
//    }
//}

//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = "mainMenu"
//    ) {
//        composable("mainMenu") {
//            MainMenuScreen(
//                onStartGame = { navController.navigate("game") }
//            )
//        }
//        composable("game") {
//            val viewModel: BalloonViewModel = hiltViewModel()
//            BalloonScreen(
//                viewModel = viewModel,
//                onExit = { navController.popBackStack() }
//            )
//        }
//    }
//}

@Composable
fun AppNavigation() {
    var hasPermission by remember { mutableStateOf(false) }

    if (hasPermission) {
        println("Katya Ballonscreen")
        BalloonScreen( hiltViewModel(), onExit = { })
    } else {
        println("Katya startscreen")

        MicrophonePermissionScreen(
            onPermissionGranted = { hasPermission = true },
            onPermissionDenied = { /* Обработка отказа */ }
        )
    }
}