package com.example.domi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val initialName = intent.getStringExtra("USER_NAME") ?: "Korisnik"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: "email@domi.com"
        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)
        
        enableEdgeToEdge()
        setContent {
            var isNightMode by remember { mutableStateOf(false) }
            var userName by remember { mutableStateOf(initialName) }

            DomiTheme(darkTheme = isNightMode) {
                MainScreen(
                    userName = userName,
                    userEmail = userEmail,
                    isAdmin = isAdmin,
                    isNightMode = isNightMode,
                    onNightModeChange = { isNightMode = it },
                    onUserNameChange = { userName = it }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userName: String,
    userEmail: String,
    isAdmin: Boolean,
    isNightMode: Boolean,
    onNightModeChange: (Boolean) -> Unit,
    onUserNameChange: (String) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isDetail = currentRoute?.startsWith("animal_detail") == true
    val isEditProfile = currentRoute == "edit_profile"
    val isFAQ = currentRoute == "faq"
    val isAboutUs = currentRoute == "about_us"
    val isAddAnimal = currentRoute == "add_animal"
    val isSpecial = isDetail || isEditProfile || isFAQ || isAboutUs || isAddAnimal
    
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context.applicationContext) }

    Scaffold(
        topBar = {
            if (currentRoute == "pets" || currentRoute == "settings" || isSpecial || currentRoute == "map" || currentRoute == "requests") {
                TopAppBar(
                    title = {
                        val titleText = when (currentRoute) {
                            "pets" -> "Ljubimci"
                            "map" -> "Skloništa"
                            "requests" -> "Zahtjevi"
                            "settings" -> "Postavke"
                            "edit_profile" -> "Uredi profil"
                            "faq" -> "Česta pitanja"
                            "about_us" -> "O nama"
                            "add_animal" -> "Dodaj ljubimca"
                            else -> "Detalji"
                        }
                        Text(text = titleText, fontWeight = FontWeight.Bold)
                    },
                    navigationIcon = {
                        if (isSpecial) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_arrow_back),
                                    contentDescription = "Nazad",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    },
                    actions = {
                        if (isAdmin && currentRoute == "pets") {
                            IconButton(onClick = { 
                                navController.navigate("add_animal")
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Dodaj životinu")
                            }
                        }
                        Text(
                            text = if (isAdmin) "ADMIN" else "Domi",
                            modifier = Modifier.padding(end = 16.dp),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp
                        )
                    }
                )
            }
        },
        bottomBar = {
            if (!isSpecial && (currentRoute == "pets" || currentRoute == "map" || currentRoute == "settings" || currentRoute == "requests")) {
                BottomNavigationBar(navController, isAdmin)
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("glavni_ekran"),
            color = MaterialTheme.colorScheme.background
        ) {
            NavigationGraph(
                navController, 
                userName, 
                userEmail, 
                isAdmin, 
                isNightMode, 
                onNightModeChange, 
                onUserNameChange,
                dbHelper
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, isAdmin: Boolean) {
    val items = mutableListOf<NavigationItem>(NavigationItem.Pets)
    if (isAdmin) {
        items.add(NavigationItem.Requests)
    } else {
        items.add(NavigationItem.Map)
    }
    items.add(NavigationItem.Settings)

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { popUpTo(it) { saveState = true } }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Pets : NavigationItem("pets", Icons.AutoMirrored.Filled.List, "Ljubimci")
    object Map : NavigationItem("map", Icons.Default.Map, "Karta")
    object Requests : NavigationItem("requests", Icons.Default.Email, "Zahtjevi")
    object Settings : NavigationItem("settings", Icons.Default.Settings, "Postavke")
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    userName: String,
    userEmail: String,
    isAdmin: Boolean,
    isNightMode: Boolean,
    onNightModeChange: (Boolean) -> Unit,
    onUserNameChange: (String) -> Unit,
    dbHelper: DatabaseHelper
) {
    var animals by remember { mutableStateOf(listOf<Animal>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        animals = withContext(Dispatchers.IO) {
            dbHelper.getAllAnimals()
        }
    }

    NavHost(navController, startDestination = "pets") {
        composable("pets") {
            AnimalListScreen(
                animals = animals,
                isAdmin = isAdmin,
                onDelete = { id ->
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            dbHelper.deleteAnimal(id)
                        }
                        val updated = withContext(Dispatchers.IO) {
                            dbHelper.getAllAnimals()
                        }
                        animals = updated
                    }
                },
                onAnimalClick = { animal ->
                    navController.navigate("animal_detail/${animal.id}")
                }
            )
        }
        composable("settings") {
            SettingsScreen(
                userName = userName,
                userEmail = userEmail,
                isNightMode = isNightMode,
                onNightModeChange = onNightModeChange,
                onEditProfileClick = { navController.navigate("edit_profile") },
                onFAQClick = { navController.navigate("faq") },
                onAboutUsClick = { navController.navigate("about_us") }
            )
        }
        composable("edit_profile") {
            EditProfileScreen(
                userEmail = userEmail,
                onBack = { navController.popBackStack() },
                onUpdateSuccess = { newName ->
                    onUserNameChange(newName)
                    navController.popBackStack()
                }
            )
        }
        composable("faq") {
            FAQScreen()
        }
        composable("about_us") {
            AboutUsScreen()
        }
        composable("map") {
            MapScreen()
        }
        composable("requests") {
            AdoptionRequestsScreen()
        }
        composable("add_animal") {
            AddAnimalScreen(
                onBack = { navController.popBackStack() },
                onAnimalAdded = {
                    scope.launch {
                        val updated = withContext(Dispatchers.IO) {
                            dbHelper.getAllAnimals()
                        }
                        animals = updated
                    }
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "animal_detail/{animalId}",
            arguments = listOf(navArgument("animalId") { type = NavType.IntType })
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getInt("animalId") ?: 0
            var animal by remember { mutableStateOf<Animal?>(null) }
            LaunchedEffect(animalId) {
                animal = withContext(Dispatchers.IO) {
                    dbHelper.getAnimalById(animalId)
                }
            }

            if (animal != null) {
                AnimalDetailScreen(
                    animal = animal!!, 
                    isAdmin = isAdmin, 
                    userName = userName, 
                    userEmail = userEmail
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    DomiTheme {
        MainScreen(
            userName = "Korisnik",
            userEmail = "email@domi.com",
            isAdmin = false,
            isNightMode = false,
            onNightModeChange = {},
            onUserNameChange = {}
        )
    }
}
