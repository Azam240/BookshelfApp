package com.example.bookshelfapp

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bookshelfapp.data.BookEntity
import com.example.bookshelfapp.viewmodel.BookViewModel
import com.example.bookshelfapp.ui.theme.BookshelfAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: BookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookshelfAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: BookViewModel) {
    var isEditing by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }

    if (isEditing) {
        BookForm(viewModel, selectedBook) {
            isEditing = false
            selectedBook = null
        }
    } else {
        var query by remember { mutableStateOf("") }
        val filteredBooks = viewModel.books.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.author.contains(query, ignoreCase = true)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📚 BookshelfApp",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "📖 Selamat Datang di BookshelfApp",
                style = MaterialTheme.typography.titleMedium
            )

            Button(
                onClick = {
                    isEditing = true
                    selectedBook = null
                }
            ) {
                Text("Tambah Buku")
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Cari buku...") },
                modifier = Modifier.fillMaxWidth()
            )

            if (viewModel.books.isEmpty()) {
                Text(
                    text = "Belum ada buku yang tersimpan.\nKlik tombol 'Tambah Buku' di atas untuk memulai.",
                    textAlign = TextAlign.Center
                )
            } else if (filteredBooks.isEmpty()) {
                Text("Tidak ada buku yang cocok.", textAlign = TextAlign.Center)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredBooks) { book ->
                        BookCard(
                            book = book,
                            onEdit = {
                                selectedBook = it
                                isEditing = true
                            },
                            onDelete = { viewModel.deleteBook(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookCard(
    book: BookEntity,
    onEdit: (BookEntity) -> Unit,
    onDelete: (BookEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Judul: ${book.title}")
            Text("Pengarang: ${book.author}")
            Text("Halaman: ${book.pages}")
            Text("Status: ${book.status}")

            if (!book.imagePath.isNullOrEmpty()) {
                AsyncImage(
                    model = book.imagePath,
                    contentDescription = null,
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onEdit(book) },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Edit")
                }
                Button(
                    onClick = { onDelete(book) },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Hapus")
                }
            }
        }
    }
}

@Composable
fun BookForm(
    viewModel: BookViewModel,
    existingBook: BookEntity? = null,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(existingBook?.title ?: "") }
    var author by remember { mutableStateOf(existingBook?.author ?: "") }
    var pages by remember { mutableStateOf(existingBook?.pages?.toString() ?: "") }
    var status by remember { mutableStateOf(existingBook?.status ?: "Belum Dibaca") }
    var imageUri by remember { mutableStateOf(existingBook?.imagePath?.let { Uri.parse(it) }) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    Column(modifier = Modifier.padding(16.dp)) {
        // Tombol kembali
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onSaved) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Judul Buku") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Pengarang") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pages,
            onValueChange = { pages = it },
            label = { Text("Jumlah Halaman") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("Status:")
        Row {
            listOf("Dibaca", "Belum Dibaca").forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = it == status,
                        onClick = { status = it }
                    )
                    Text(it)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { imagePicker.launch("image/*") }) {
            Text("Pilih Sampul Buku")
        }

        imageUri?.let {
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (title.isNotBlank() && pages.isNotBlank()) {
                    val book = BookEntity(
                        id = existingBook?.id ?: 0,
                        title = title,
                        author = author,
                        pages = pages.toIntOrNull() ?: 0,
                        status = status,
                        imagePath = imageUri?.toString() ?: ""
                    )
                    if (existingBook != null) {
                        viewModel.updateBook(book)
                        Toast.makeText(context, "Buku berhasil diupdate", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.insert(book)
                        Toast.makeText(context, "Buku berhasil disimpan", Toast.LENGTH_SHORT).show()
                    }
                    onSaved()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (existingBook != null) "Update" else "Simpan")
        }
    }
}
