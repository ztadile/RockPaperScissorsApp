package com.example.rockpaperscissorsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rockpaperscissorsapp.ui.theme.RockPaperScissorsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RockPaperScissorsAppTheme {
                RockPaperScissorsGame()
            }
        }
    }
}

@Composable
fun RockPaperScissorsGame() {
    var wins by remember { mutableStateOf(0) }
    var losses by remember { mutableStateOf(0) }
    var gamesPlayed by remember { mutableStateOf(0) }
    var resultText by remember { mutableStateOf("Make your choice!") }
    var resultColor by remember { mutableStateOf(Color.Black) }
    var aiChoice by remember { mutableStateOf("") }
    var animateResult by remember { mutableStateOf(false) }
    var showAIChoice by remember { mutableStateOf(false) }
    var avatarRotation by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Player: Eli", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        // ✅ Corrected syntax for avatar image rotation click
        Image(
            painter = painterResource(id = R.drawable.eli_avatar),
            contentDescription = "Player Avatar",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 24.dp)
                .rotate(avatarRotation)
                .clickable { avatarRotation += 360f }
        )

        Text(
            text = "Wins: $wins | Losses: $losses | Played: $gamesPlayed",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Result Text Fade-in Animation
        val alphaAnim by animateFloatAsState(
            targetValue = if (animateResult) 1f else 0f,
            animationSpec = tween(durationMillis = 500),
            finishedListener = { animateResult = false }
        )

        Text(
            text = resultText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = resultColor,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .graphicsLayer { alpha = alphaAnim }
        )

        // AI Choice Slide-In Animation
        AnimatedVisibility(visible = showAIChoice) {
            Text(
                text = "AI chose: $aiChoice",
                fontSize = 18.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("Rock", "Paper", "Scissors").forEach { choice ->
                AnimatedGameButton(
                    choice = choice,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val choices = listOf("Rock", "Paper", "Scissors")
                        val aiMove = choices.random()
                        aiChoice = aiMove
                        showAIChoice = false
                        resultText = determineResult(choice, aiMove).also { result ->
                            resultColor = when (result) {
                                "Win" -> Color.Green
                                "Lose" -> Color.Red
                                else -> Color.Gray
                            }
                            if (result == "Win") wins++
                            if (result == "Lose") losses++
                        }
                        gamesPlayed++
                        animateResult = true
                        showAIChoice = true
                    }
                )
            }
        }
    }
}

@Composable
fun AnimatedGameButton(choice: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    var scale by remember { mutableStateOf(1f) }

    Button(
        onClick = {
            scale = 1.2f
            onClick()
        },
        modifier = modifier
            .padding(8.dp)
            .scale(scale)
    ) {
        Text(text = choice)
    }

    LaunchedEffect(scale) {
        if (scale > 1f) {
            animate(
                initialValue = scale,
                targetValue = 1f,
                animationSpec = tween(150)
            ) { value, _ -> scale = value }
        }
    }
}

fun determineResult(playerChoice: String, aiChoice: String): String {
    return when {
        playerChoice == aiChoice -> "Draw"
        (playerChoice == "Rock" && aiChoice == "Scissors") ||
                (playerChoice == "Paper" && aiChoice == "Rock") ||
                (playerChoice == "Scissors" && aiChoice == "Paper") -> "Win"
        else -> "Lose"
    }
}

@Preview(showBackground = true)
@Composable
fun GamePreview() {
    RockPaperScissorsAppTheme {
        RockPaperScissorsGame()
    }
}
