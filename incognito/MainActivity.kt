package com.example.incognito

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography

import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.incognito.ViewModel.IncognitoNavigation
import com.example.incognito.ui.theme.IncognitoTheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.incognito.ViewModel.MainViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        enableEdgeToEdge()
        setContent {
            val viewModel = viewModel<MainViewModel>()
            IncognitoTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize().systemBarsPadding()
                ) {
                    IncognitoNavigation(viewModel = viewModel)
                }
            }
        }
    }
}

private val MyLightColorScheme = lightColorScheme(
    primary = Color(0xFF60B5FF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFAFDDFF),  // Added
    onPrimaryContainer = Color.White,      // Added
    secondary = Color(0xFFFFECDB),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFF9149), // Added
    onSecondaryContainer = Color.White,     // Added
    tertiary = Color(0xFF018786),           // Added for Incognito
    onTertiary = Color.White,               // Added
    background = Color(0xFFAFDDFF),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFFFECDB),     // Added
    onSurfaceVariant = Color.Black,          // Added
    error = Color(0xFFFF9149),               // Added
    onError = Color.White                    // Added
)

@Composable
fun IncognitoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MyLightColorScheme, // Your color scheme
        typography = MyTypography,         // Your typography
        shapes = MyShapes,                 // Your shapes
        content = content
    )
}

// Custom font family (you can add more if needed)
val MyTypography = Typography(  // Remove androidx.compose.material prefix
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
    )
)

// Custom Shapes (rounded corner radii for various UI elements)
val MyShapes = Shapes(
    small = RoundedCornerShape(4.dp),    // for small elements like buttons
    medium = RoundedCornerShape(8.dp),   // for medium elements like cards
    large = RoundedCornerShape(16.dp)    // for larger elements like dialogs
)
