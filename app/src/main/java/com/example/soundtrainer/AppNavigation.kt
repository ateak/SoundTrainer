package com.example.soundtrainer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.soundtrainer.presentation.GameScreen
import com.example.soundtrainer.presentation.StartScreen

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AppNavigation() {
    val viewModel: GameViewModel = hiltViewModel()
    var gameStarted by rememberSaveable { mutableStateOf(false) }

    if (gameStarted) {
        GameScreen(viewModel, onExit = { gameStarted = false })
    } else {
        StartScreen(
            onStartGame = { gameStarted = true },
            hiltViewModel()
        )
    }
}


//TODO добавить попозже navigation

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
