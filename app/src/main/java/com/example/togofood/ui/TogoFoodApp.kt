package com.example.togofood.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.togofood.ui.navigation.NavGraph
import com.example.togofood.ui.theme.TogoFoodTheme

@Composable
fun TogoFoodApp() {
    TogoFoodTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            val navController = rememberNavController()
            NavGraph(navController = navController)
        }
    }
}
