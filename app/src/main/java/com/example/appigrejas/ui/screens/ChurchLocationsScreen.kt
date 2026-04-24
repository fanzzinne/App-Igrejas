package com.example.appigrejas.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appigrejas.ui.theme.Gold

data class ChurchBranch(
    val name: String,
    val address: String,
    val phone: String,
    val isHeadquarters: Boolean = false,
    val mapsUrl: String
)

@Composable
fun ChurchLocationsScreen(windowSizeClass: WindowSizeClass) {
    val context = LocalContext.current
    val isExpanded = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    val branches = listOf(
        ChurchBranch(
            name = "Igreja Sede",
            address = "Av. Principal, 1000 - Centro, São Paulo - SP",
            phone = "(11) 99999-9999",
            isHeadquarters = true,
            mapsUrl = "https://maps.google.com/?q=Igreja+Sede"
        ),
        ChurchBranch(
            name = "Filial Zona Norte",
            address = "Rua das Flores, 500 - Santana, São Paulo - SP",
            phone = "(11) 88888-8888",
            mapsUrl = "https://maps.google.com/?q=Filial+Zona+Norte"
        ),
        ChurchBranch(
            name = "Filial Zona Sul",
            address = "Av. Interlagos, 2500 - Interlagos, São Paulo - SP",
            phone = "(11) 77777-7777",
            mapsUrl = "https://maps.google.com/?q=Filial+Zona+Sul"
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(if (isExpanded) 0.6f else 1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Nossas Igrejas",
                    color = Gold,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Encontre a igreja mais próxima de você.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            items(branches) { branch ->
                BranchCard(branch) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(branch.mapsUrl))
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun BranchCard(branch: ChurchBranch, onMapClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (branch.isHeadquarters) 2.dp else 0.dp,
                color = if (branch.isHeadquarters) Gold else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (branch.isHeadquarters) Icons.Default.Place else Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = branch.name,
                        color = Gold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (branch.isHeadquarters) {
                        Text(
                            text = "IGREJA SEDE",
                            color = Gold.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = branch.address, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = branch.phone, color = Color.Gray, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onMapClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = if (branch.isHeadquarters) Gold else Color.Transparent),
                shape = RoundedCornerShape(8.dp),
                border = if (!branch.isHeadquarters) androidx.compose.foundation.BorderStroke(1.dp, Gold) else null
            ) {
                Text(
                    text = "Ver no Mapa",
                    color = if (branch.isHeadquarters) Color.Black else Gold,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
