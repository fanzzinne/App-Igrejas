package com.example.appigrejas

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import com.example.appigrejas.data.remote.BannerResponse
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppIgrejasTheme {
                MainScreen()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Início", Icons.Default.Home)
    object Media : Screen("media", "Mídia", Icons.Default.PlayCircle)
    object Bible : Screen("bible", "Bíblia", Icons.AutoMirrored.Filled.MenuBook)
    object Community : Screen("community", "Comunidade", Icons.Default.Groups)
    object Devotional : Screen("devotional", "Devocional", Icons.Default.Favorite)
    object Giving : Screen("giving", "Contribuição", Icons.Default.Paid)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(homeViewModel: HomeViewModel = viewModel()) {
    val navController = rememberNavController()
    val uiState by homeViewModel.uiState.collectAsState()
    val items = listOf(
        Screen.Home,
        Screen.Media,
        Screen.Bible,
        Screen.Community
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Gold.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Gold)
                        ) {
                            if (uiState?.config?.LogoUrl != null) {
                                AsyncImage(
                                    model = uiState?.config?.LogoUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.padding(4.dp)
                                )
                            } else {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Church, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = uiState?.config?.NomeIgreja ?: "App Igrejas",
                            color = Gold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notificações */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Gold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                contentColor = Gold
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
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
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { 
                HomeScreen(
                    viewModel = homeViewModel,
                    onActionClick = { action ->
                        when (action) {
                            "Bíblia" -> navController.navigate(Screen.Bible.route)
                            "Oração" -> {
                                navController.navigate(Screen.Community.route)
                            }
                            "Contribuição" -> navController.navigate(Screen.Giving.route)
                            "Ao Vivo" -> {
                                // Handled in QuickActionsGrid
                            }
                            "Eventos" -> navController.navigate(Screen.Community.route)
                        }
                    },
                    onDevotionalClick = {
                        navController.navigate(Screen.Devotional.route)
                    }
                ) 
            }
            composable(Screen.Media.route) { SermonLibraryScreen() }
            composable(Screen.Bible.route) { BibleTabScreen() }
            composable(Screen.Community.route) { CommunityScreen() }
            composable(Screen.Devotional.route) { DevotionalScreen() }
            composable(Screen.Giving.route) { DigitalGivingScreen(homeViewModel) }
        }
    }
}

@Composable
fun BibleTabScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Bíblia", "Devocional")

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Black,
            contentColor = Gold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Gold
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title, color = if (selectedTabIndex == index) Gold else Color.Gray) }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> BibleScreen()
            1 -> DevotionalScreen()
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onActionClick: (String) -> Unit = {},
    onDevotionalClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val localContext = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Banner Carousel
        item {
            BannerCarousel(uiState?.banners ?: emptyList())
        }

        // Quick Actions
        item {
            QuickActionsGrid(onActionClick = { action ->
                if (action == "Ao Vivo") {
                    val liveUrl = uiState?.config?.LinkAoVivo ?: "https://www.youtube.com/results?search_query=igreja+ao+vivo"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(liveUrl))
                    localContext.startActivity(intent)
                } else {
                    onActionClick(action)
                }
            })
        }

        // Humor Bar
        item {
            HumorBar(onMoodClick = { mood ->
                onActionClick("Bíblia")
                // In a real app, we would pass the mood to the Bible/Devotional screen via a SharedViewModel or Navigation argument
            })
        }

        // News/Events Horizontal Grid
        item {
            SectionTitle("Notícias e Eventos")
            NewsHorizontalGrid()
        }

        // Devotional Shortcut
        item {
            SectionTitle("Devocional de Hoje")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = onDevotionalClick,
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "O Renovo das Misericórdias", color = Gold, fontWeight = FontWeight.Bold)
                        Text(text = "Lamentações 3:22-23", color = Color.Gray, fontSize = 12.sp)
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Gold)
                }
            }
        }

        // Footer
        item {
            AppFooter()
        }
    }
}

@Composable
fun BannerCarousel(banners: List<BannerResponse>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
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
fun QuickActionsGrid(onActionClick: (String) -> Unit) {
    val actions = listOf(
        QuickAction("Ao Vivo", Icons.Default.LiveTv),
        QuickAction("Oração", Icons.Default.VolunteerActivism),
        QuickAction("Contribuição", Icons.Default.Paid),
        QuickAction("Eventos", Icons.Default.CalendarMonth)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        actions.forEach { action ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
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

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun MainScreenPreview() {
    AppIgrejasTheme {
        MainScreen()
    }
}
