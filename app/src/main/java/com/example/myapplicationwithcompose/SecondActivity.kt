package com.example.myapplicationwithcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavType
import androidx.navigation.compose.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument   // ← this fixes navArgument + type



class MoviesViewModel : ViewModel() {
    private val _movies = MutableStateFlow<List<MovieEntity>>(emptyList())
    val movies: StateFlow<List<MovieEntity>> = _movies

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var ascending = true

    private suspend fun saveMoviesToDb(movies: List<Movie>, context: android.content.Context) {
        val db = AppDatabase.getDatabase(context)
        val movieEntities = movies.map { movie ->
            MovieEntity(
                title = movie.title,
                releaseDate = "1111",
                imdbRating = movie.imdbRating,
                director = "Unknown",
                duration = 90,
                genre = "Animation"
            )
        }
        db.movieDao().deleteAll()
        db.movieDao().insertAll(movieEntities)
    }

    private suspend fun loadMoviesFromDb(context: android.content.Context): List<MovieEntity> {
        val db = AppDatabase.getDatabase(context)
        return db.movieDao().getAll()
    }

    fun loadMovies(context: android.content.Context) {
        viewModelScope.launch {
            try {
                val dbMovies = loadMoviesFromDb(context)
                if (dbMovies.isNotEmpty()) {
                    _movies.value = dbMovies
                    _isLoading.value = false
                    return@launch
                }
                
                val apiMovies = RetrofitClient.api.getMovies()
                saveMoviesToDb(apiMovies, context)
                _movies.value = loadMoviesFromDb(context)
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleSort() {
        ascending = !ascending
        _movies.value = if (ascending)
            _movies.value.sortedBy { it.title }
        else
            _movies.value.sortedByDescending { it.title }
    }
}

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "list") {
                        composable("list") {
                            SecondScreen(
                                onMovieClick = { movie ->
                                    navController.navigate("detail/${movie.title}")
                                }
                            )
                        }
                        composable(
                            "detail/{title}",
                            arguments = listOf(navArgument("title") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val title = backStackEntry.arguments?.getString("title") ?: ""
                            val context = LocalContext.current
                            val db = AppDatabase.getDatabase(context)

//                            val movie by remember(title) {
//                                derivedStateOf {
//                                    db.movieDao().getAll().find { it.title == title }
//                                }
//                            }

                            val movie by produceState<MovieEntity?>(initialValue = null, title) {
                                value = db.movieDao().getAll().find { it.title == title }
                            }


                            if (movie != null) {
                                MovieDetailScreen(
                                    movie = movie!!,
                                    navController = navController,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecondScreen(vm: MoviesViewModel = viewModel(), onMovieClick: (MovieEntity) -> Unit) {
    val movies by vm.movies.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    var ascending by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.loadMovies(context)
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(1f).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Список фильмов",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = {
                        ascending = !ascending
                        vm.toggleSort()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text(if (ascending) "А → Я" else "Я → А", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                when {
                    isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(error!!, color = Color.Red)
                    }
                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(movies) { movie ->
                            MovieItem(movie = movie, onClick = { onMovieClick(movie) })
                        }
                    }
                }
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Список фильмов",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EE),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    ascending = !ascending
                    vm.toggleSort()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text(if (ascending) "А → Я" else "Я → А", color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error!!, color = Color.Red)
                }
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(movies) { movie ->
                        MovieItem(movie = movie, onClick = { onMovieClick(movie) })
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: MovieEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(movie.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Дата выхода: ${movie.releaseDate}", fontSize = 14.sp, color = Color.Gray)
            Text("Рейтинг IMDb: ${movie.imdbRating}", fontSize = 14.sp, color = Color.Gray)
            Text("Режиссёр: ${movie.director}", fontSize = 14.sp, color = Color.Gray)
            Text("Длительность: ${movie.duration} мин", fontSize = 14.sp, color = Color.Gray)
            Text("Жанр: ${movie.genre}", fontSize = 14.sp, color = Color.Gray)
        }
    }
}
