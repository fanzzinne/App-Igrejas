package com.example.appigrejas.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appigrejas.ui.components.AppFooter
import com.example.appigrejas.ui.theme.Gold
import com.example.appigrejas.viewmodel.HomeViewModel

@Composable
fun DigitalGivingScreen(homeViewModel: HomeViewModel = viewModel()) {
    val uiState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val pixKey = uiState?.config?.ChavePix ?: "00.000.000/0001-00"

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableIntStateOf(0) } // 0: Dízimo, 1: Oferta

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Contribuição Digital",
                color = Gold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Chave PIX (CNPJ)", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        text = pixKey,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Chave PIX", pixKey)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Chave PIX copiada!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Gold),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Copiar Chave", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            TabRow(
                selectedTabIndex = selectedType,
                containerColor = Color.Black,
                contentColor = Gold,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedType]),
                        color = Gold
                    )
                }
            ) {
                Tab(selected = selectedType == 0, onClick = { selectedType = 0 }) {
                    Text("Dízimo", modifier = Modifier.padding(16.dp), color = if(selectedType == 0) Gold else Color.Gray)
                }
                Tab(selected = selectedType == 1, onClick = { selectedType = 1 }) {
                    Text("Oferta", modifier = Modifier.padding(16.dp), color = if(selectedType == 1) Gold else Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome Completo", color = Gold) },
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
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Valor R$", color = Gold) },
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
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (amount.isNotBlank()) {
                        Toast.makeText(context, "Informação de envio registrada!", Toast.LENGTH_LONG).show()
                        name = ""
                        amount = ""
                    } else {
                        Toast.makeText(context, "Informe o valor", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Gold)
            ) {
                Text(text = "Informar Envio", color = Gold, fontWeight = FontWeight.Bold)
            }
        }

        item {
            AppFooter()
        }
    }
}
