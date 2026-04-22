package com.example.appigrejas.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appigrejas.ui.components.AppFooter
import com.example.appigrejas.ui.theme.Gold

@Composable
fun DigitalGivingScreen() {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val pixKey = "00.000.000/0001-00" // Exemplo de CNPJ
    val bankInfo = "Banco: 001 - Banco do Brasil\nAgência: 1234-5\nConta: 67890-1\nCNPJ: 00.000.000/0001-00"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Contribuição Digital",
            color = Gold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Sua generosidade ajuda a manter nossa missão e ministérios.",
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Pix Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.QrCode2, contentDescription = null, tint = Gold, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Chave PIX (CNPJ)", color = Gold, fontWeight = FontWeight.Bold)
                Text(text = pixKey, color = Color.White, fontSize = 18.sp)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { clipboardManager.setText(AnnotatedString(pixKey)) },
                    colors = ButtonDefaults.buttonColors(containerColor = Gold),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Copiar Chave", color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bank Deposit Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Dados Bancários", color = Gold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = bankInfo, color = Color.White, fontSize = 14.sp, lineHeight = 22.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Receipt Submission
        Text(
            text = "Já fez sua contribuição?",
            color = Gold,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = "Envie o comprovante para nosso financeiro.",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:financeiro@igreja.com")
                    putExtra(Intent.EXTRA_SUBJECT, "Comprovante de Contribuição")
                }
                context.startActivity(Intent.createChooser(intent, "Enviar comprovante..."))
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = androidx.compose.foundation.BorderStroke(1.dp, Gold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Email, contentDescription = null, tint = Gold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Anexar Comprovante", color = Gold)
        }

        AppFooter()
    }
}
