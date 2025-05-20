import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.*//added nomizo
import androidx.compose.ui.ExperimentalComposeUiApi//added
import androidx.compose.ui.focus.focusRequester//added
import androidx.compose.ui.platform.LocalFocusManager//added
import androidx.compose.ui.platform.LocalSoftwareKeyboardController//added
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.incognito.ViewModel.MainViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InfoScreen(navController: NavController, onNavigateToGame: (Int) -> Unit,
               viewModel : MainViewModel
) {
    val isDark by viewModel.isDarkMode.collectAsState()
    var playerCount by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var rulesDialog by remember { mutableStateOf(false) }
    // Add language state
    var isEnglish by remember { mutableStateOf(true) }

    // Create a function to handle the game start logic
    val startGame = {
        val count = playerCount.toIntOrNull()
        if (count != null && count in 4..19) {
            keyboardController?.hide()
            onNavigateToGame(count)
        } else {
            isError = true
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.Black/*MaterialTheme.colorScheme.background*/)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    keyboardController?.hide()
                }.imePadding(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {



            TypewriterText(
                text = "Welcome to the Game!",
                typingSpeed = 85, // faster typing
                style = TextStyle(fontSize = 30.sp,
                    fontWeight = FontWeight.Bold)
            )
            OutlinedTextField(
                value = playerCount,
                onValueChange = { newText ->
                    /*Text(
                        text = "Welcome to the Game!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )*/
                    if (newText.all { it.isDigit() } || newText.isEmpty()) {
                        playerCount = newText
                        isError = false
                    } else {
                        isError = true
                    }
                },
                shape = RoundedCornerShape(16.dp),
                label = { Text("Number of Players") },
                placeholder = { Text("Enter a number...") },
                isError = isError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done  // Set the keyboard action to Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { startGame() }  // Execute the startGame function when Done/Enter is pressed
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.Black/*MaterialTheme.colorScheme.surface*/).imePadding()
            )

            if (isError) {
                Text(
                    text = "Please enter a valid number between 4 and 19",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(180.dp))

            Button(
                onClick = { startGame() },  // Use the same startGame function for consistency
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Start Game",
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }


            // Modern Rules button with text instead of icon
            Button(
                onClick = { rulesDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Rules",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }

    if(rulesDialog){
        AlertDialog(
            onDismissRequest = { rulesDialog = false },
            title = { Text("Rules") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp).imePadding()
                ) {
                    // Language selection buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { isEnglish = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isEnglish)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) {
                            Text(
                                "English",
                                color = if (isEnglish)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { isEnglish = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isEnglish)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        ) {
                            Text(
                                "Ελληνικά",
                                color = if (!isEnglish)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Dynamic rules text based on language
                    Text(
                        text = if (isEnglish) {
                            "Welcome to Incognito!\n" +
                                    "A game designed by students, for students—the goal is simple: Have fun! \uD83C\uDF89\n" +
                                    "\n" +
                                    "How to Play:\n" +
                                    "Teams: Players are divided into three teams, but no one knows their team at the start!\n" +
                                    "Gameplay: Each round, players say one word related to a secret topic, then vote to eliminate someone.\n" +
                                    "Team Roles:\n" +
                                    "Incognito (Solo Player):\n" +
                                    "\n" +
                                    "Only knows they are Incognito.\n" +
                                    "Must blend in and avoid being discovered.\n" +
                                    "Police (Majority Team):\n" +
                                    "\n" +
                                    "Knows a secret word.\n" +
                                    "Must identify teammates and eliminate non-police players.\n" +
                                    "Win by eliminating all Incognitos & Undercovers.\n" +
                                    "Undercovers (Smaller Team, but never just one):\n" +
                                    "\n" +
                                    "Know another similar secret word.\n" +
                                    "Must find each other and eliminate all non-undercover players to win.\n" +
                                    "\uD83D\uDD0D The Challenge? No one knows their team from the start! You must figure it out through conversations and strategic voting.\n" +
                                    "\n" +
                                    "\uD83D\uDDF3 Each round, everyone says a word, then votes to eliminate a player. Keep your team safe, deceive your opponents, and win the game!\n" +
                                    "\n" +
                                    "\uD83D\uDE80 Good luck & have fun!\n" +
                                    "\n"
                        } else {
                            "Καλώς ήρθατε στο Incognito!\n" +
                                    "Ένα παιχνίδι σχεδιασμένο από φοιτητές, για φοιτητές—ο στόχος είναι απλός: Διασκεδάστε! \uD83C\uDF89\n" +
                                    "\n" +
                                    "Πώς παίζεται:\n" +
                                    "Ομάδες: Οι παίκτες χωρίζονται σε τρεις ομάδες, αλλά κανείς δεν γνωρίζει την ομάδα του στην αρχή!\n" +
                                    "Gameplay: Κάθε γύρο, οι παίκτες λένε μια λέξη σχετική με ένα μυστικό θέμα, και μετά ψηφίζουν για να αποβάλλουν κάποιον.\n" +
                                    "Ρόλοι Ομάδων:\n" +
                                    "Incognito (Μοναχικός Παίκτης):\n" +
                                    "\n" +
                                    "Γνωρίζει μόνο ότι είναι Incognito.\n" +
                                    "Πρέπει να προσαρμοστεί και να αποφύγει την ανακάλυψη.\n" +
                                    "Αστυνομία (Πλειοψηφική Ομάδα):\n" +
                                    "\n" +
                                    "Γνωρίζει μια μυστική λέξη.\n" +
                                    "Πρέπει να εντοπίσει τους συμπαίκτες και να αποβάλει τους μη-αστυνομικούς παίκτες.\n" +
                                    "Κερδίζει αποβάλλοντας όλους τους Incognitos & Undercovers.\n" +
                                    "Undercovers (Μικρότερη Ομάδα, αλλά ποτέ μόνο ένας):\n" +
                                    "\n" +
                                    "Γνωρίζουν μια άλλη (παρόμοια) μυστική λέξη.\n" +
                                    "Πρέπει να βρουν ο ένας τον άλλον και να αποβάλουν όλους τους μη-undercover παίκτες για να κερδίσουν.\n" +
                                    "\uD83D\uDD0D Η Πρόκληση; Κανείς δεν γνωρίζει την ομάδα του από την αρχή! Πρέπει να το καταλάβετε μέσω συζητήσεων και στρατηγικής ψηφοφορίας.\n" +
                                    "\n" +
                                    "\uD83D\uDDF3 Κάθε γύρο, όλοι λένε μια λέξη, μετά ψηφίζουν για να αποβάλουν έναν παίκτη. Κρατήστε την ομάδα σας ασφαλή, παραπλανήστε τους αντιπάλους σας, και κερδίστε το παιχνίδι!\n" +
                                    "\n" +
                                    "\uD83D\uDE80 Καλή τύχη & καλή διασκέδαση!\n" +
                                    "\n"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { rulesDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (isEnglish) "Close" else "Κλείσιμο")
                }
            }
        )
    }
}

@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    typingSpeed: Long = 100, // milliseconds per character
    style: TextStyle = MaterialTheme.typography.headlineMedium
) {
    var visibleText by remember { mutableStateOf("") }
    val textLength = text.length

    LaunchedEffect(text) {
        visibleText = ""

        for (i in text.indices) {
            delay(typingSpeed)
            visibleText = text.substring(0, i + 1)
        }
    }

    Text(
        text = visibleText,
        modifier = modifier,
        style = style
    )
}