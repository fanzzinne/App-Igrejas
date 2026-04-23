package com.example.appigrejas.ui.screens

import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.appigrejas.data.model.LeaderMessage
import com.example.appigrejas.data.model.Ministry
import com.example.appigrejas.data.remote.EventResponse
import com.example.appigrejas.ui.components.AppFooter
import com.example.appigrejas.ui.theme.Gold
import com.example.appigrejas.util.VideoUtils
import com.example.appigrejas.viewmodel.CommunityViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CommunityScreen(
    windowSizeClass: WindowSizeClass,
    tabIndex: Int = 0,
    viewModel: CommunityViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(tabIndex) }
    val tabs = listOf("Ministérios", "Liderança", "Agenda Semanal", "Pedidos de Oração")
    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Black,
            contentColor = Gold,
            edgePadding = 16.dp,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Gold
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(text = title, fontSize = 14.sp, color = if (selectedTab == index) Gold else Color.Gray) }
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> MinistryList(viewModel, isExpanded)
                1 -> LeaderMessagesList(viewModel, isExpanded)
                2 -> WeeklyAgendaList(viewModel, isExpanded)
                3 -> PrayerRequestScreen(viewModel, isExpanded)
            }
        }
    }
}

@Composable
fun WeeklyAgendaList(viewModel: CommunityViewModel, isExpanded: Boolean) {
    val events by viewModel.events.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(if (isExpanded) 2 else 1),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(events) { event ->
            AgendaCard(event)
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            AppFooter()
        }
    }
}

@Composable
fun AgendaCard(event: EventResponse) {
    // Função para formatar data/hora para o padrão Brasil
    fun formatToBR(value: String?, isTime: Boolean = false): String {
        if (value == null || value.isBlank() || value.lowercase() == "undefined") return ""
        
        return try {
            // Tenta detectar se é uma data completa ISO (comum em APIs)
            val inputFormat = if (value.contains("T")) {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            } else if (value.contains("-")) {
                SimpleDateFormat("yyyy-MM-dd", Locale.US)
            } else {
                null
            }

            if (inputFormat != null) {
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(value)
                val outputFormat = if (isTime) {
                    SimpleDateFormat("HH:mm", Locale("pt", "BR"))
                } else {
                    SimpleDateFormat("dd/MM", Locale("pt", "BR"))
                }
                outputFormat.timeZone = TimeZone.getTimeZone("America/Sao_Paulo")
                date?.let { outputFormat.format(it) } ?: value
            } else {
                value // Se não for data ISO, retorna o texto original (ex: "Domingo")
            }
        } catch (e: Exception) {
            value
        }
    }

    val dia = formatToBR(event.Data?.takeIf { it.isNotBlank() } ?: event.Dia)
    val titulo = event.Titulo?.takeIf { it.isNotBlank() } ?: event.Evento ?: ""
    val hora = formatToBR(event.Horario?.takeIf { it.isNotBlank() } ?: event.Hora, isTime = true)
    val local = event.Local?.takeIf { it.lowercase() != "undefined" } ?: ""

    if (titulo.isBlank() && dia.isBlank()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Gold.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Event, contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = titulo,
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                if (dia.isNotBlank() || hora.isNotBlank() || local.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (dia.isNotBlank()) {
                            Text(text = dia, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        if (dia.isNotBlank() && (hora.isNotBlank() || local.isNotBlank())) {
                            Text(text = "  •  ", color = Gold, fontSize = 14.sp)
                        }
                        if (hora.isNotBlank()) {
                            Text(text = hora, color = Color.White, fontSize = 14.sp)
                        }
                        if (hora.isNotBlank() && local.isNotBlank()) {
                            Text(text = "  •  ", color = Gold, fontSize = 14.sp)
                        }
                        if (local.isNotBlank()) {
                            Text(text = local, color = Color.LightGray, fontSize = 14.sp, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MinistryList(viewModel: CommunityViewModel, isExpanded: Boolean) {
    val ministries by viewModel.ministries.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(if (isExpanded) 3 else 1),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(ministries) { ministry ->
            MinistryCard(ministry)
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            AppFooter()
        }
    }
}

@Composable
fun MinistryCard(ministry: Ministry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            AsyncImage(
                model = ministry.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(150.dp)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = ministry.name, color = Gold, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Líder: ${ministry.leader}", color = Color.LightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = ministry.description, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun LeaderMessagesList(viewModel: CommunityViewModel, isExpanded: Boolean) {
    val messages by viewModel.messages.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(if (isExpanded) 2 else 1),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(messages) { message ->
            LeaderMessageCard(message)
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            AppFooter()
        }
    }
}

@Composable
fun LeaderMessageCard(message: LeaderMessage) {
    val embedUrl = remember(message.videoUrl) { VideoUtils.getEmbedUrl(message.videoUrl) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.2f))
    ) {
        Column {
            if (embedUrl != null) {
                VideoPlayer(url = embedUrl, modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f))
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = message.author, color = Gold, fontWeight = FontWeight.Bold)
                        Text(text = message.date, color = Color.Gray, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message.title,
                    color = Gold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message.content,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun VideoPlayer(url: String, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                loadUrl(url)
            }
        },
        modifier = modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    )
}

@Composable
fun PrayerRequestScreen(viewModel: CommunityViewModel, isExpanded: Boolean) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = if (isExpanded) Alignment.CenterHorizontally else Alignment.Start
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (isExpanded) 0.6f else 1f)
                    .padding(horizontal = if (isExpanded) 0.dp else 0.dp)
            ) {
                PrayerRequestForm(viewModel)
            }
        }
        item { AppFooter() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerRequestForm(viewModel: CommunityViewModel) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Geral") }
    var message by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Saúde", "Família", "Finanças", "Trabalho", "Relacionamento", "Geral")
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text(text = "Fale conosco", color = Gold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = "Deixe seu pedido de oração e nossa equipe estará intercedendo por você.", color = Color.Gray, fontSize = 14.sp)
        
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Seu Nome", color = Gold) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold,
                unfocusedBorderColor = Gold.copy(alpha = 0.5f),
                focusedLabelColor = Gold,
                cursorColor = Gold,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoria", color = Gold) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold,
                    unfocusedBorderColor = Gold.copy(alpha = 0.5f),
                    focusedLabelColor = Gold,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFF1A1A1A))
            ) {
                categories.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption, color = Gold) },
                        onClick = {
                            category = selectionOption
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Seu Pedido", color = Gold) },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold,
                unfocusedBorderColor = Gold.copy(alpha = 0.5f),
                focusedLabelColor = Gold,
                cursorColor = Gold,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        var isSending by remember { mutableStateOf(false) }

        Button(
            onClick = {
                if (name.isNotBlank() && message.isNotBlank()) {
                    isSending = true
                    coroutineScope.launch {
                        val success = viewModel.submitPrayerRequest(
                            name = name,
                            phone = "",
                            category = category,
                            message = message
                        )
                        isSending = false
                        if (success) {
                            Toast.makeText(context, "Pedido de oração enviado!", Toast.LENGTH_LONG).show()
                            name = ""
                            message = ""
                        } else {
                            Toast.makeText(context, "Erro ao enviar pedido. Tente novamente.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isSending,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Gold),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isSending) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
            } else {
                Text(text = "Enviar Pedido", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
