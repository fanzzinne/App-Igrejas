package com.example.appigrejas

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import com.example.appigrejas.data.remote.BannerResponse
import com.example.appigrejas.data.remote.ConfigResponse
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.appigrejas.ui.components.AppFooter
import com.example.appigrejas.ui.screens.*
import com.example.appigrejas.ui.theme.AppIgrejasTheme
import com.example.appigrejas.ui.theme.Gold
import com.example.appigrejas.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            AppIgrejasTheme {
                MainScreen(windowSizeClass = windowSizeClass)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Início", Icons.Default.Home)
    object Media : Screen("media", "Mídia", Icons.Default.PlayCircle)
    object Bible : Screen("bible", "Bíblia", Icons.AutoMirrored.Filled.MenuBook)
    object Community : Screen("community", "Comunidade", Icons.Default.Groups)
    object Giving : Screen("giving", "Contribuição", Icons.Default.Paid)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(
        factory = androidx.lifecycle.viewmodel.MutableCreationExtras().apply {
            set(androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY, context.applicationContext as android.app.Application)
        }.let { extras ->
            object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(context.applicationContext as android.app.Application) as T
                }
            }
        }
    )
    val navController = rememberNavController()
    val uiState by homeViewModel.uiState.collectAsState()
    val localContext = LocalContext.current
    
    val defaultLogo = "https://i.ibb.co/tMg1KQyD/Logo-dourado.png"
    val defaultName = "Nome da Sua Igreja Aqui"

    // Função para extrair as configurações independente se vier como objeto ou lista
    val churchConfig = remember(uiState) {
        fun Map<*, *>.findKey(vararg keys: String): String? {
            for (key in keys) {
                // Tenta a chave exata
                val value = this[key] as? String
                if (!value.isNullOrBlank() && value.lowercase() != "undefined") return value.trim()
                
                // Tenta a chave em minúsculo
                val lowerValue = this[key.lowercase()] as? String
                if (!lowerValue.isNullOrBlank() && lowerValue.lowercase() != "undefined") return lowerValue.trim()

                // Procura por qualquer chave que contenha o nome ignorando caso
                val foundKey = this.keys.find { it.toString().equals(key, ignoreCase = true) }
                if (foundKey != null) {
                    val anyValue = this[foundKey] as? String
                    if (!anyValue.isNullOrBlank() && anyValue.lowercase() != "undefined") return anyValue.trim()
                }
            }
            return null
        }

        val configData = uiState?.config ?: uiState?.configuracoes
        val map = when (configData) {
            is Map<*, *> -> configData
            is List<*> -> configData.firstOrNull() as? Map<*, *>
            else -> null
        }

        if (map != null) {
            ConfigResponse(
                NomeIgreja = map.findKey("NomeIgreja", "Nome", "Igreja"),
                LogoUrl = map.findKey("LogoUrl", "Logo", "Imagem", "Url"),
                LinkAoVivo = map.findKey("LinkAoVivo", "AoVivo", "Live", "Video"),
                ChavePix = map.findKey("ChavePix", "Pix", "Chave"),
                LinkGaleria = map.findKey("LinkGaleria", "Galeria", "Fotos"),
                VersiculoDia = map.findKey("VersiculoDia", "Versiculo", "Palavra"),
                ReferenciaDia = map.findKey("ReferenciaDia", "Referencia", "Texto")
            )
        } else {
            ConfigResponse(NomeIgreja = defaultName, LogoUrl = defaultLogo)
        }
    }

    val items = listOf(
        Screen.Home,
        Screen.Media,
        Screen.Bible,
        Screen.Community
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    val isMedium = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium

    Row(modifier = Modifier.fillMaxSize()) {
        // Navigation Rail for Medium and Expanded (Tablet/Desktop)
        if (isMedium || isExpanded) {
            NavigationRail(
                containerColor = Color.Black,
                contentColor = Gold,
                header = {
                    Box(
                        modifier = Modifier.size(100.dp).padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = churchConfig.LogoUrl ?: defaultLogo,
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            ) {
                Spacer(modifier = Modifier.weight(1f))
                items.forEach { screen ->
                    NavigationRailItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = Gold,
                            indicatorColor = Gold,
                            unselectedIconColor = Gold.copy(alpha = 0.6f),
                            unselectedTextColor = Gold.copy(alpha = 0.6f)
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Scaffold(
            topBar = {
                Surface(
                    color = Color.Black,
                    modifier = Modifier.statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp) // Aumentado ligeiramente para acomodar o logo maior sem ser excessivo como antes
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (!(isMedium || isExpanded)) {
                            Box(
                                modifier = Modifier.size(90.dp), // Logo aumentado para ser visível mas dentro da área reduzida
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = churchConfig.LogoUrl ?: defaultLogo,
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else {
                            // Se for tablet, apenas um espaçador para manter o botão à direita
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        Button(
                            onClick = {
                                val liveUrl = churchConfig.LinkAoVivo ?: "https://www.youtube.com/results?search_query=igreja+ao+vivo"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(liveUrl))
                                localContext.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp).background(Color.White.copy(alpha = 0.2f), CircleShape).padding(2.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AO VIVO",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                if (!(isMedium || isExpanded)) {
                    NavigationBar(
                        containerColor = Color.Black,
                        contentColor = Gold
                    ) {
                        items.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = null) },
                                label = { Text(screen.title) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = Gold,
                                    indicatorColor = Gold,
                                    unselectedIconColor = Gold.copy(alpha = 0.6f),
                                    unselectedTextColor = Gold.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        windowSizeClass = windowSizeClass,
                        viewModel = homeViewModel,
                        onActionClick = { action ->
                            when (action) {
                                "Bíblia" -> navController.navigate(Screen.Bible.route)
                                "Oração" -> {
                                    navController.navigate(Screen.Community.route + "?tab=3")
                                }
                                "Contribuição" -> navController.navigate(Screen.Giving.route)
                                "Mural" -> {
                                    navController.navigate(Screen.Community.route + "?tab=99")
                                }
                                "Ministérios" -> {
                                    navController.navigate(Screen.Community.route + "?tab=1")
                                }
                                "Agenda Semanal" -> navController.navigate(Screen.Community.route + "?tab=2")
                            }
                        },
                        onMoodClick = { mood ->
                            navController.navigate(Screen.Community.route + "?tab=0&mood=$mood")
                        }
                    )
                }
                composable(Screen.Media.route) { SermonLibraryScreen(windowSizeClass) }
                composable(Screen.Bible.route) { BibleScreen(windowSizeClass) }
                composable(
                    route = Screen.Community.route + "?tab={tab}&mood={mood}",
                    arguments = listOf(
                        navArgument("tab") { 
                            type = NavType.IntType
                            defaultValue = -1 // Default para o Hub
                        },
                        navArgument("mood") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null 
                        }
                    )
                ) { backStackEntry ->
                    val tabIndex = backStackEntry.arguments?.getInt("tab") ?: -1
                    val mood = backStackEntry.arguments?.getString("mood")
                    CommunityScreen(windowSizeClass, tabIndex = tabIndex, mood = mood, config = churchConfig)
                }
                composable(Screen.Giving.route) { DigitalGivingScreen(windowSizeClass, homeViewModel) }
            }
        }
    }
}

@Composable
fun HomeScreen(
    windowSizeClass: WindowSizeClass,
    viewModel: HomeViewModel = viewModel(),
    onActionClick: (String) -> Unit = {},
    onMoodClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val hasNewMural by viewModel.hasNewMural.collectAsState()
    val localContext = LocalContext.current

    LaunchedEffect(hasNewMural) {
        if (hasNewMural) {
            android.widget.Toast.makeText(localContext, "Nova mensagem postada no Mural da Liderança!", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Banner Carousel
        item {
            BannerCarousel(uiState?.banners ?: emptyList(), isExpanded)
        }

        item {
            QuickActionsGrid(
                isExpanded, 
                hasNewMural = viewModel.hasNewMural.collectAsState().value,
                onActionClick = { action ->
                    if (action == "Mural") viewModel.markMuralAsSeen()
                    
                    if (action == "Ao Vivo") {
                    val configData = uiState?.config ?: uiState?.configuracoes
                    val liveUrl = when(configData) {
                        is Map<*, *> -> configData["LinkAoVivo"] as? String ?: configData["AoVivo"] as? String
                        is List<*> -> (configData.firstOrNull() as? Map<*, *>)?.get("LinkAoVivo") as? String ?: (configData.firstOrNull() as? Map<*, *>)?.get("AoVivo") as? String
                        else -> null
                    } ?: "https://www.youtube.com/results?search_query=igreja+ao+vivo"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(liveUrl))
                    localContext.startActivity(intent)
                } else {
                    onActionClick(action)
                }
            })
        }

        // Humor Bar
        item {
            HumorBar(onMoodClick = onMoodClick)
        }

        // News/Events Horizontal Grid
        item {
            SectionTitle("Notícias e Agenda")
            NewsHorizontalGrid()
        }

        // Ticker de Agenda Semanal
        item {
            AgendaTicker(uiState?.eventos?.takeIf { it.isNotEmpty() } ?: uiState?.agenda ?: emptyList())
        }

        // Footer
        item {
            AppFooter()
        }
    }
}

@Composable
fun BannerCarousel(banners: List<BannerResponse>, isExpanded: Boolean = false) {
    val height = if (isExpanded) 400.dp else 200.dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp) // Removido padding superior para subir o banner
            .clip(RoundedCornerShape(16.dp))
            .background(Color.DarkGray)
    ) {
        if (banners.isNotEmpty()) {
            val banner = banners[0]
            AsyncImage(
                model = banner.ImagemUrl,
                contentDescription = "Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Text(
                text = banner.Titulo,
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        } else {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1438232992991-995b7058bbb3?q=80&w=1000",
                contentDescription = "Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Text(
                text = "Culto de Celebração\nDomingo às 19h",
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun QuickActionsGrid(isExpanded: Boolean = false, hasNewMural: Boolean = false, onActionClick: (String) -> Unit) {
    val actions = listOf(
        QuickAction("Mural", Icons.Default.Campaign),
        QuickAction("Ministérios", Icons.Default.Groups),
        QuickAction("Contribuição", Icons.Default.Paid),
        QuickAction("Agenda Semanal", Icons.Default.CalendarMonth)
    )

    val arrangement = if (isExpanded) Arrangement.Center else Arrangement.SpaceEvenly
    val spacing = if (isExpanded) 48.dp else 16.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = arrangement
    ) {
        actions.forEach { action ->
            if (isExpanded) Spacer(modifier = Modifier.width(spacing))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BadgedBox(
                    badge = {
                        if (action.label == "Mural" && hasNewMural) {
                            Badge(containerColor = Color.Red) {
                                Text("!", color = Color.White)
                            }
                        }
                    }
                ) {
                    Surface(
                        onClick = { onActionClick(action.label) },
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = Gold
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.label,
                                tint = Color.Black
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = action.label,
                    color = Gold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class QuickAction(val label: String, val icon: ImageVector)

@Composable
fun HumorBar(onMoodClick: (String) -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(32.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Como você está se sentindo hoje?",
                color = Gold,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = { onMoodClick("Feliz") }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.SentimentVerySatisfied, contentDescription = "Feliz", tint = Gold)
                }
                IconButton(onClick = { onMoodClick("Triste") }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.SentimentVeryDissatisfied, contentDescription = "Triste", tint = Gold)
                }
                IconButton(onClick = { onMoodClick("Grato") }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = "Grato", tint = Gold)
                }
                IconButton(onClick = { onMoodClick("Cansado") }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.BatteryAlert, contentDescription = "Cansado", tint = Gold)
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Gold,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun NewsHorizontalGrid() {
    val news = listOf(
        "Conferência de Jovens 2026",
        "Novo Horário de Cultos",
        "Ação Social no Sábado",
        "Escola Bíblica Dominical"
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(news) { item ->
            Card(
                modifier = Modifier
                    .width(200.dp)
                    .height(120.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?q=80&w=500",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                    )
                    Text(
                        text = item,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AgendaTicker(events: List<com.example.appigrejas.data.remote.EventResponse>) {
    if (events.isEmpty()) return

    // Função local para formatar data/hora (respeitando o que foi digitado ou convertendo ISO para GMT-3)
    fun formatToBR(value: String?, isTime: Boolean = false): String {
        if (value == null || value.isBlank() || value.lowercase() == "undefined") return ""
        
        // Se já tiver formato de data BR (ex: 22/04) ou hora (ex: 19:00), retorna como está
        if (value.contains("/") && value.length <= 10) return value
        if (value.contains(":") && !value.contains("T") && value.length <= 5) return value

        return try {
            val inputFormat = if (value.contains("T")) {
                java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
            } else if (value.contains("-") && value.length > 8) {
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
            } else null

            if (inputFormat != null) {
                inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(value)
                val outputFormat = if (isTime) {
                    java.text.SimpleDateFormat("HH:mm", java.util.Locale("pt", "BR"))
                } else {
                    java.text.SimpleDateFormat("dd/MM", java.util.Locale("pt", "BR"))
                }
                outputFormat.timeZone = java.util.TimeZone.getTimeZone("America/Sao_Paulo")
                date?.let { outputFormat.format(it) } ?: value
            } else value
        } catch (e: Exception) { value }
    }

    val tickerText = remember(events) {
        events.joinToString("   ✦   ") { ev -> // Separador mais elegante
            val titulo = ev.Titulo?.takeIf { it.isNotBlank() } ?: ev.Evento ?: ""
            val dia = formatToBR(ev.Data?.takeIf { it.isNotBlank() } ?: ev.Dia)
            val hora = formatToBR(ev.Horario?.takeIf { it.isNotBlank() } ?: ev.Hora, isTime = true)
            val info = listOfNotNull(dia.takeIf { it.isNotBlank() }, hora.takeIf { it.isNotBlank() }).joinToString(" às ")
            "${titulo.uppercase()} ${if (info.isNotBlank()) "($info)" else ""}"
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ticker")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = 1.5f, // Inicia um pouco mais longe
        targetValue = -1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 40000, easing = LinearEasing), // Mais lento (40s)
            repeatMode = RepeatMode.Restart
        ),
        label = "xOffset"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = Color(0xFF1A1A1A),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Gold.copy(alpha = 0.2f))
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
            val width = maxWidth
            Text(
                text = tickerText,
                color = Gold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(x = xOffset * width),
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Tela de $name", color = Gold, fontSize = 24.sp)
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun MainScreenMobilePreview() {
    AppIgrejasTheme {
        MainScreen(windowSizeClass = WindowSizeClass.calculateFromSize(androidx.compose.ui.unit.DpSize(411.dp, 891.dp)))
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun MainScreenTabletPreview() {
    AppIgrejasTheme {
        MainScreen(windowSizeClass = WindowSizeClass.calculateFromSize(androidx.compose.ui.unit.DpSize(1280.dp, 800.dp)))
    }
}
