package com.example.domi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)
        enableEdgeToEdge()
        setContent {
            var isNightMode by remember { mutableStateOf(false) }

            DomiTheme(darkTheme = isNightMode) {
                MainScreen(
                    isAdmin = isAdmin,
                    isNightMode = isNightMode,
                    onNightModeChange = { isNightMode = it }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isAdmin: Boolean,
    isNightMode: Boolean,
    onNightModeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isDetail = currentRoute?.startsWith("animal_detail") == true
    val context = LocalContext.current

    Scaffold(
        topBar = {
            if (currentRoute == "pets" || currentRoute == "settings" || isDetail) {
                TopAppBar(
                    title = {
                        val titleText = when (currentRoute) {
                            "pets" -> "Ljubimci"
                            "map" -> "Skloništa"
                            "settings" -> "Postavke"
                            else -> "Detalji"
                        }
                        Text(text = titleText, fontWeight = FontWeight.Bold)
                    },
                    navigationIcon = {
                        if (isDetail) {
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
                                Toast.makeText(context, "Funkcija dodavanja u izradi", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Dodaj životinju")
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
            if (currentRoute == "pets" || currentRoute == "map" || currentRoute == "settings") {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            NavigationGraph(navController, isAdmin, isNightMode, onNightModeChange)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        NavigationItem.Pets,
        NavigationItem.Map,
        NavigationItem.Settings
    )
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
    object Settings : NavigationItem("settings", Icons.Default.Settings, "Postavke")
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    isAdmin: Boolean,
    isNightMode: Boolean,
    onNightModeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    var animals by remember { mutableStateOf(listOf<Animal>()) }

    LaunchedEffect(Unit) {
        animals = dbHelper.getAllAnimals()
    }

    NavHost(navController, startDestination = "pets") {
        composable("pets") {
            AnimalListScreen(
                animals = animals,
                isAdmin = isAdmin,
                onDelete = { id ->
                    dbHelper.deleteAnimal(id)
                    animals = dbHelper.getAllAnimals()
                },
                onAnimalClick = { animal ->
                    navController.navigate("animal_detail/${animal.id}")
                }
            )
        }
        composable("settings") {
            SettingsScreen(isNightMode = isNightMode, onNightModeChange = onNightModeChange)
        }
        composable("map") {
            MapScreen()
        }
        composable(
            route = "animal_detail/{animalId}",
            arguments = listOf(navArgument("animalId") { type = NavType.IntType })
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getInt("animalId") ?: 0
            var animal by remember { mutableStateOf<Animal?>(null) }
            LaunchedEffect(animalId) {
                animal = dbHelper.getAnimalById(animalId)
            }

            if (animal != null) {
                AnimalDetailScreen(animal = animal!!, isAdmin = isAdmin)
            }
        }
    }
}

@Composable
fun AnimalListScreen(
    animals: List<Animal>,
    isAdmin: Boolean,
    onDelete: (Int) -> Unit,
    onAnimalClick: (Animal) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(animals) { animal ->
            Box {
                AnimalCard(animal) { onAnimalClick(animal) }
                if (isAdmin) {
                    IconButton(
                        onClick = { onDelete(animal.id) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Obriši",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimalDetailScreen(animal: Animal, isAdmin: Boolean) {
    var showForm by remember { mutableStateOf(false) }
    
    if (showForm) {
        AdoptionFormScreen(animalName = animal.name) { showForm = false }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(id = animal.imageRes),
                    contentDescription = "Slika ljubimca",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = animal.name, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${animal.breed}, ${animal.age}", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "O ljubimcu", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = animal.description, fontSize = 16.sp, lineHeight = 24.sp)
                }
            }

            if (!isAdmin) {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { showForm = true },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("UDOMI ME", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MapScreen() {
    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body { margin: 0; padding: 0; }
                #map { height: 100vh; width: 100vw; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map').setView([45.8150, 15.9819], 7);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '© OpenStreetMap'
                }).addTo(map);

                var shelters = [
                    { pos: [45.8300, 16.1400], name: "Sklonište Dumovec" },
                    { pos: [46.3900, 16.4350], name: "Sklonište Prijatelji Čakovec" },
                    { pos: [45.5550, 18.6750], name: "Sklonište za pse Osijek" }
                ];

                shelters.forEach(function(s) {
                    L.marker(s.pos).addTo(map).bindPopup(s.name);
                });
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun AdoptionFormScreen(animalName: String, onBack: () -> Unit) {
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Obrazac za udomljavanje", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Ljubimac: $animalName", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Zašto želite udomiti ovog ljubimca?") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 5
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                Toast.makeText(context, "Zahtjev poslan!", Toast.LENGTH_LONG).show()
                onBack()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("POŠALJI ZAHTJEV")
        }
        
        TextButton(onClick = onBack) {
            Text("Odustani")
        }
    }
}

@Composable
fun AnimalCard(animal: Animal, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = animal.imageRes),
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = animal.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = animal.breed, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = animal.age, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
