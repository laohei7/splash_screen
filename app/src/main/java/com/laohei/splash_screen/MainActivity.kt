package com.laohei.splash_screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.laohei.splash_screen.ui.theme.Splash_screenTheme
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Splash_screenTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Route.Splash
                ) {
                    // 设置跳转主页
                    composable<Route.Splash> { SplashScreen(navController) }
                    composable<Route.Home> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Home")
                        }
                    }
                }
            }
        }
    }
}

@Serializable
sealed interface Route {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Home : Route
}

@Preview
@Composable
fun SplashScreen(
    navController: NavHostController = rememberNavController()
) {

    var step by remember { mutableStateOf<SplashStep>(SplashStep.None) }

    val transition = updateTransition(targetState = step, label = "transition")

    // 设置动画
    LaunchedEffect(Unit) {
        step = SplashStep.Start
        delay(800)
        step = SplashStep.Loading
        delay(800)
        step = SplashStep.End
        // 等待 End 动画播放玩后跳转
        delay(600)
        navController.navigate(Route.Home) {
            popUpTo<Route.Splash> { inclusive = true } // 移除 splash screen
            launchSingleTop = true // 设置主页为单例
        }
    }

    val alpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) },
        label = "alpha"
    ) {
        when (it) {
            SplashStep.None,
            SplashStep.End -> 0f

            SplashStep.Loading,
            SplashStep.Start -> 1f
        }
    }

    val scale by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) },
        label = "scale"
    ) {
        when (it) {
            SplashStep.None,
            SplashStep.End -> 0f

            SplashStep.Loading,
            SplashStep.Start -> 1f
        }
    }

    val loadingWidth by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600) },
        label = "width"
    ) {
        when (it) {
            SplashStep.Start,
            SplashStep.None,
            SplashStep.End -> 0f

            SplashStep.Loading -> 0.8f

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo_light),
                contentDescription = "logo",
                modifier = Modifier.graphicsLayer {
                    this.alpha = alpha
                    scaleY = scale
                    scaleX = scale
                }
            )
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(loadingWidth)
                    .graphicsLayer {
                        this.alpha = alpha
                    },
                thickness = 3.dp,
                color = Color.Red
            )
        }
    }
}

sealed interface SplashStep {
    data object None : SplashStep
    data object Start : SplashStep
    data object Loading : SplashStep
    data object End : SplashStep
}