package com.example.bookshelfapp.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelfapp.data.AppDatabase
import com.example.bookshelfapp.data.BookEntity
import kotlinx.coroutines.launch

class BookViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getInstance(app).bookDao()
    private val _books = mutableStateListOf<BookEntity>()
    val books: List<BookEntity> get() = _books

    fun loadBooks() {
        viewModelScope.launch {
            _books.clear()
            _books.addAll(dao.getAll())
        }
    }

    fun insert(book: BookEntity) {
        viewModelScope.launch {
            dao.insert(book)
            loadBooks()
        }
    }

    fun updateBook(book: BookEntity) {
        viewModelScope.launch {
            dao.update(book)
            loadBooks()
        }
    }

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch {
            dao.delete(book)
            loadBooks()
        }
    }
}
