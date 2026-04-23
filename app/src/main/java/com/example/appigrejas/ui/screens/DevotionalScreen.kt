package com.example.appigrejas.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appigrejas.ui.theme.Gold

data class MoodVerse(val title: String, val text: String, val reference: String)

val DEVOTIONAL_VERSES = mapOf(
    "Triste" to listOf(
        MoodVerse("O Consolo que Vem do Alto", "Perto está o Senhor dos que têm o coração quebrantado e salva os de espírito oprimido.", "Salmos 34:18"),
        MoodVerse("Deus Enxugará suas Lágrimas", "Ele enxugará dos seus olhos toda lágrima. Não haverá mais morte, nem tristeza, nem choro, nem dor.", "Apocalipse 21:4"),
        MoodVerse("Refúgio e Fortaleza", "Deus é o nosso refúgio e a nossa fortaleza, auxílio sempre presente na adversidade.", "Salmos 46:1")
    ),
    "Cansado" to listOf(
        MoodVerse("Descanso para a Alma", "Vinde a mim, todos os que estais cansados e oprimidos, e eu vos aliviarei.", "Mateus 11:28"),
        MoodVerse("Forças Renovadas", "Mas aqueles que esperam no Senhor renovam as suas forças. Voam alto como águias; correm e não se fatigam.", "Isaías 40:31"),
        MoodVerse("Socorro Bem Presente", "Elevo os meus olhos para os montes; de onde vem o meu socorro? O meu socorro vem do Senhor, que fez o céu e a terra.", "Salmos 121:1-2")
    ),
    "Grato" to listOf(
        MoodVerse("O Sacrifício de Louvor", "Em tudo dai graças, porque esta é a vontade de Deus em Cristo Jesus para convosco.", "1 Tessalonicenses 5:18"),
        MoodVerse("A Bondade do Senhor", "Deem graças ao Senhor, porque ele é bom; o seu amor dura para sempre.", "Salmos 107:1"),
        MoodVerse("Louvor de Coração", "Bendiga ao Senhor a minha alma! Bendiga ao seu santo nome todo o meu ser!", "Salmos 103:1")
    ),
    "Feliz" to listOf(
        MoodVerse("A Alegria do Senhor", "Não vos entristeçais, porque a alegria do Senhor é a vossa força.", "Neemias 8:10"),
        MoodVerse("Coração Alegre", "O coração alegre aformoseia o rosto, mas pela dor do coração o espírito se abate.", "Provérbios 15:13"),
        MoodVerse("Regozijo Constante", "Alegrem-se sempre no Senhor. Novamente direi: Alegrem-se!", "Filipenses 4:4")
    )
)

val DEFAULT_VERSE = MoodVerse(
    "O Renovo das Misericórdias",
    "As misericórdias do Senhor são a causa de não sermos consumidos, porque as suas compaixões não têm fim; renovam-se cada manhã.",
    "Lamentações 3:22-23"
)

@Composable
fun DevotionalScreen(windowSizeClass: WindowSizeClass? = null, mood: String? = null) {
    val context = LocalContext.current
    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded
    
    // Pick a random verse based on mood once per composition
    val verseData = remember(mood) {
        val list = DEVOTIONAL_VERSES[mood]
        if (list != null && list.isNotEmpty()) {
            list.random()
        } else {
            DEFAULT_VERSE
        }
    }
    
    val fullShareText = "${verseData.title}\n\n\"${verseData.text}\"\n\n${verseData.reference}"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(if (isExpanded) 0.6f else 0.9f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (mood != null) {
                Text(
                    text = "Sinto-me $mood",
                    color = Gold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "\"${verseData.text}\"",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = verseData.reference,
                        color = Gold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, fullShareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Compartilhar Devocional")
                    context.startActivity(shareIntent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Gold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.wrapContentWidth()
            ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Compartilhar", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
