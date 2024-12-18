// File: app/src/main/java/com/example/socialswig/ui/screens/TimedModeScreen.kt

package com.example.socialswig.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.socialswig.viewmodel.GameViewModel
import com.example.socialswig.ui.navigation.Screen
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimedModeScreen(navController: NavController, viewModel: GameViewModel) {
    val questions by viewModel.questions.collectAsState()
    val score by viewModel.score.collectAsState()
    val shouldNavigateToExit by viewModel.shouldNavigateToExit.collectAsState()
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var timer by remember { mutableStateOf(30) } // 30 seconds per challenge

    LaunchedEffect(key1 = currentQuestionIndex) {
        timer = 30
        while (timer > 0 && currentQuestionIndex < questions.size) {
            delay(1000L)
            timer--
        }
        // Timer has run out
        // Handle timer expiration, e.g., increment score or skip
    }

    if (shouldNavigateToExit) {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.Exit.route) {
                popUpTo(Screen.TimedMode.route) { inclusive = true }
            }
            viewModel.resetNavigationFlag()
        }
    } else if (currentQuestionIndex >= questions.size) {
        // All questions done, navigate to result
        LaunchedEffect(Unit) {
            navController.navigate(Screen.Result.route) {
                popUpTo(Screen.TimedMode.route) { inclusive = true }
            }
        }
    } else {
        val question = questions.getOrNull(currentQuestionIndex)
        if (question == null) {
            Log.e("TimedModeScreen", "Question at index $currentQuestionIndex is null")
            return
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Timed Mode") },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Time Remaining: $timer sec",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = question.text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.incrementScore(1) // Increase score by 1 for each completed question
                        viewModel.nextQuestion() // No need to pass navController here
                        currentQuestionIndex++
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Completed", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}