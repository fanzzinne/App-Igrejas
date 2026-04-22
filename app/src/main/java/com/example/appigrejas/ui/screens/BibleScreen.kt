package com.example.appigrejas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appigrejas.ui.components.AppFooter
import com.example.appigrejas.ui.theme.Gold
import com.example.appigrejas.viewmodel.BibleViewModel

@Composable
fun BibleScreen(viewModel: BibleViewModel = viewModel()) {
    val verses by viewModel.verses.collectAsState()
    val books by viewModel.books.collectAsState()
    val selectedBook by viewModel.selectedBook.collectAsState()
    val selectedChapter by viewModel.selectedChapter.collectAsState()
    val selectedVersion by viewModel.selectedVersion.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showBookDialog by remember { mutableStateOf(false) }
    var showChapterDialog by remember { mutableStateOf(false) }
    var showVersionDialog by remember { mutableStateOf(false) }

    val versions = mapOf("NVIPT" to "NVI", "ARAV" to "ARA", "ARC" to "ARC", "KJV" to "KJV")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(
                text = "Bíblia Sagrada",
                color = Gold,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showVersionDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(0.6f),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(text = versions[selectedVersion] ?: selectedVersion, color = Gold, fontSize = 12.sp)
                }
                Button(
                    onClick = { showBookDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Text(text = selectedBook.name, color = Gold, maxLines = 1, fontSize = 12.sp)
                }
                Button(
                    onClick = { showChapterDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(0.7f)
                ) {
                    Text(text = "Cap. $selectedChapter", color = Gold, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Gold)
                }
            }
        } else if (verses.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Nenhum versículo encontrado.", color = Color.Gray)
                }
            }
        } else {
            items(verses) { verse ->
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    Text(
                        text = "${verse.verse} ",
                        color = Gold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = verse.text,
                        color = Color.White,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                }
            }
        }
        
        item {
            AppFooter()
        }
    }

    // Dialogs
    if (showVersionDialog) {
        AlertDialog(
            onDismissRequest = { showVersionDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Selecionar Versão", color = Gold) },
            text = {
                LazyColumn {
                    items(versions.toList()) { (code, name) ->
                        TextButton(
                            onClick = {
                                viewModel.setVersion(code)
                                showVersionDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(name, color = if (selectedVersion == code) Gold else Color.White)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showBookDialog) {
        AlertDialog(
            onDismissRequest = { showBookDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Selecionar Livro", color = Gold) },
            text = {
                LazyColumn(modifier = Modifier.height(400.dp)) {
                    items(books) { book ->
                        TextButton(
                            onClick = {
                                viewModel.setBook(book)
                                showBookDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(book.name, color = if (selectedBook.id == book.id) Gold else Color.White)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showChapterDialog) {
        AlertDialog(
            onDismissRequest = { showChapterDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Selecionar Capítulo", color = Gold) },
            text = {
                val chapters = (1..selectedBook.chapters).toList()
                LazyColumn(modifier = Modifier.height(400.dp)) {
                    items(chapters.chunked(4)) { rowChapters ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            rowChapters.forEach { chapter ->
                                TextButton(onClick = {
                                    viewModel.setChapter(chapter)
                                    showChapterDialog = false
                                }) {
                                    Text(chapter.toString(), color = if (selectedChapter == chapter) Gold else Color.White)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}
