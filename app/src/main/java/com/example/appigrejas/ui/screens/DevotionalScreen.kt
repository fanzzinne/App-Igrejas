package com.example.appigrejas.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appigrejas.ui.components.AppFooter
import com.example.appigrejas.ui.theme.Gold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class MoodVerse(val title: String, val text: String, val reference: String)

// Referências curadas para o Devocional
val DEVOTIONAL_REFERENCES = listOf(
    "João+3:16", "Salmos+23:1", "I+Coríntios+13:4", "Filipenses+4:13", "Mateus+11:28",
    "Isaías+41:10", "Jeremias+29:11", "Romanos+8:28", "Provérbios+3:5", "Josué+1:9",
    "Salmos+46:1", "Mateus+6:33", "Gálatas+5:22", "Efésios+2:8", "Lamentações+3:22"
)

@Composable
fun DevotionalScreen(
    windowSizeClass: WindowSizeClass? = null,
    mood: String? = null,
    config: com.example.appigrejas.data.remote.ConfigResponse? = null
) {
    val context = LocalContext.current
    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded
    
    var bibleVerse by remember { mutableStateOf<MoodVerse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Função para buscar versículo da API
    suspend fun fetchVerse(ref: String): MoodVerse? {
        return withContext(Dispatchers.IO) {
            try {
                // Usando a tradução 'almeida' para Português
                val response = URL("https://bible-api.com/$ref?translation=almeida").readText()
                val json = JSONObject(response)
                val text = json.getString("text").trim()
                val reference = json.getString("reference")
                MoodVerse("Palavra de Deus", text, reference)
            } catch (e: Exception) {
                null
            }
        }
    }

    LaunchedEffect(Unit) {
        val randomRef = DEVOTIONAL_REFERENCES.random()
        bibleVerse = fetchVerse(randomRef)
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Gold)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(if (isExpanded) 0.6f else 1f)
                            .padding(bottom = 32.dp),
                        color = Color(0xFF1A1A1A),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "DEVOCIONAL",
                                color = Gold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            bibleVerse?.let { verse ->
                                Text(
                                    text = "\"${verse.text}\"",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontStyle = FontStyle.Italic,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 28.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = verse.reference,
                                    color = Gold,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } ?: Text("Não foi possível carregar o devocional.", color = Color.Gray)
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Button(
                                    onClick = {
                                        bibleVerse?.let { verse ->
                                            val sendIntent: Intent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, "${verse.text}\n\n${verse.reference}")
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(sendIntent, "Compartilhar"))
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Gold),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Compartilhar", color = Color.Black)
                                }
                            }
                        }
                    }
                }
                
                item {
                    AppFooter()
                }
            }
        }
    }
}
