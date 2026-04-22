package com.example.appigrejas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appigrejas.data.model.BibleBook
import com.example.appigrejas.data.model.BibleVerse
import com.example.appigrejas.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BibleViewModel : ViewModel() {
    private val bibleApi = RetrofitClient.bibleApi

    private val _books = MutableStateFlow<List<BibleBook>>(getBibleBooksFallback())
    val books: StateFlow<List<BibleBook>> = _books

    private val _verses = MutableStateFlow<List<BibleVerse>>(emptyList())
    val verses: StateFlow<List<BibleVerse>> = _verses

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedVersion = MutableStateFlow("NVIPT")
    val selectedVersion: StateFlow<String> = _selectedVersion

    private val _selectedBook = MutableStateFlow(getBibleBooksFallback()[0])
    val selectedBook: StateFlow<BibleBook> = _selectedBook

    private val _selectedChapter = MutableStateFlow(1)
    val selectedChapter: StateFlow<Int> = _selectedChapter

    init {
        loadVerses()
    }

    fun setVersion(version: String) {
        _selectedVersion.value = version
        loadVerses()
    }

    fun setBook(book: BibleBook) {
        _selectedBook.value = book
        _selectedChapter.value = 1
        loadVerses()
    }

    fun setChapter(chapter: Int) {
        _selectedChapter.value = chapter
        loadVerses()
    }

    fun loadVerses() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = bibleApi.getChapter(
                    _selectedVersion.value,
                    _selectedBook.value.id,
                    _selectedChapter.value
                )
                _verses.value = result
            } catch (e: Exception) {
                _verses.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getBibleBooksFallback() = listOf(
        BibleBook(1, "Gênesis", "gn", 50),
        BibleBook(2, "Êxodo", "ex", 40),
        BibleBook(3, "Levítico", "lv", 27),
        BibleBook(4, "Números", "nm", 36),
        BibleBook(5, "Deuteronômio", "dt", 34),
        BibleBook(6, "Josué", "js", 24),
        BibleBook(7, "Juízes", "jz", 21),
        BibleBook(8, "Rute", "rt", 4),
        BibleBook(9, "1 Samuel", "1sm", 31),
        BibleBook(10, "2 Samuel", "2sm", 24),
        BibleBook(11, "1 Reis", "1rs", 22),
        BibleBook(12, "2 Reis", "2rs", 25),
        BibleBook(13, "1 Crônicas", "1cr", 29),
        BibleBook(14, "2 Crônicas", "2cr", 36),
        BibleBook(15, "Esdras", "ezr", 10),
        BibleBook(16, "Neemias", "ne", 13),
        BibleBook(17, "Ester", "et", 10),
        BibleBook(18, "Jó", "job", 42),
        BibleBook(19, "Salmos", "ps", 150),
        BibleBook(20, "Provérbios", "pr", 31),
        BibleBook(21, "Eclesiastes", "ec", 12),
        BibleBook(22, "Cânticos", "ct", 8),
        BibleBook(23, "Isaías", "is", 66),
        BibleBook(24, "Jeremias", "jr", 52),
        BibleBook(25, "Lamentações", "lm", 5),
        BibleBook(26, "Ezequiel", "ez", 48),
        BibleBook(27, "Daniel", "dn", 12),
        BibleBook(28, "Oseias", "os", 14),
        BibleBook(29, "Joel", "jl", 3),
        BibleBook(30, "Amós", "am", 9),
        BibleBook(31, "Obadias", "ob", 1),
        BibleBook(32, "Jonas", "jn", 4),
        BibleBook(33, "Miqueias", "mi", 7),
        BibleBook(34, "Naum", "na", 3),
        BibleBook(35, "Habacuque", "hb", 3),
        BibleBook(36, "Sofonias", "sf", 3),
        BibleBook(37, "Ageu", "ag", 2),
        BibleBook(38, "Zacarias", "zc", 14),
        BibleBook(39, "Malaquias", "ml", 4),
        BibleBook(40, "Mateus", "mt", 28),
        BibleBook(41, "Marcos", "mk", 16),
        BibleBook(42, "Lucas", "lk", 24),
        BibleBook(43, "João", "jn", 21),
        BibleBook(44, "Atos", "act", 28),
        BibleBook(45, "Romanos", "rm", 16),
        BibleBook(46, "1 Coríntios", "1co", 16),
        BibleBook(47, "2 Coríntios", "2co", 13),
        BibleBook(48, "Gálatas", "gl", 6),
        BibleBook(49, "Efésios", "ep", 6),
        BibleBook(50, "Filipenses", "ph", 4),
        BibleBook(51, "Colossenses", "cl", 4),
        BibleBook(52, "1 Tessalonicenses", "1ts", 5),
        BibleBook(53, "2 Tessalonicenses", "2ts", 3),
        BibleBook(54, "1 Timóteo", "1tm", 6),
        BibleBook(55, "2 Timóteo", "2tm", 4),
        BibleBook(56, "Tito", "tt", 3),
        BibleBook(57, "Filemom", "phm", 1),
        BibleBook(58, "Hebreus", "hb", 13),
        BibleBook(59, "Tiago", "tg", 5),
        BibleBook(60, "1 Pedro", "1pe", 5),
        BibleBook(61, "2 Pedro", "2pe", 3),
        BibleBook(62, "1 João", "1jn", 5),
        BibleBook(63, "2 João", "2jn", 1),
        BibleBook(64, "3 João", "3jn", 1),
        BibleBook(65, "Judas", "jd", 1),
        BibleBook(66, "Apocalipse", "re", 22)
    ).map { if (it.id == 37) it.copy(abbrev = "ag") else it }
}
