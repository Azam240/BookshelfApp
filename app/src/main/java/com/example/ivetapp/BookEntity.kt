package com.example.bookshelfapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val author: String,
    val pages: Int,
    val status: String, // Dibaca / Belum Dibaca
    val imagePath: String? = null
)
