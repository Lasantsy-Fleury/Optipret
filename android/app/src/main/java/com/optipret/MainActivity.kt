package com.optipret

import android.graphics.Color as AndroidColor
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.optipret.data.local.DataStoreManager
import com.optipret.repository.LoanRepository
import com.optipret.ui.navigation.NavigationHost
import com.optipret.ui.theme.Shapes
import com.optipret.ui.viewmodel.LoanViewModel
import com.optipret.ui.viewmodel.SettingsViewModel
import com.optipret.ui.viewmodel.StatisticsViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.statusBarColor = AndroidColor.TRANSPARENT

    splashScreen.setOnExitAnimationListener { splashScreenView ->
      val fadeOut = ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)
      fadeOut.duration = 300L
      fadeOut.doOnEnd { splashScreenView.remove() }
      fadeOut.start()
    }

    setContent {
      AppTheme {
        val repository = LoanRepository()
        val dataStore = DataStoreManager(applicationContext)

        val loanViewModel: LoanViewModel = viewModel(
          factory = LoanViewModel.provideFactory(repository)
        )
        val statisticsViewModel: StatisticsViewModel = viewModel(
          factory = StatisticsViewModel.provideFactory(repository)
        )
        val settingsViewModel: SettingsViewModel = viewModel(
          factory = SettingsViewModel.provideFactory(dataStore)
        )

        NavigationHost(
          loanViewModel = loanViewModel,
          statisticsViewModel = statisticsViewModel,
          settingsViewModel = settingsViewModel
        )
      }
    }
  }
}

@Composable
private fun AppTheme(content: @Composable () -> Unit) {
  val colorScheme = lightColorScheme(
    primary = Color(0xFF2563EB),
    onPrimary = Color.White,
    background = Color(0xFFF9FAFB),
    onBackground = Color(0xFF111827),
    surface = Color(0xFFF9FAFB),
    onSurface = Color(0xFF111827)
  )

  MaterialTheme(
    colorScheme = colorScheme,
    shapes = Shapes,
    content = content
  )
}
