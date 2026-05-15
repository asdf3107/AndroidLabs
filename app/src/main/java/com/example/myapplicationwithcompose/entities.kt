package com.example.myapplicationwithcompose

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val login: String,
    val password: String
)

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val title: String,
    val releaseDate: String,
    val imdbRating: Double,
    val director: String = "Unknown",
    val duration: Int = 0,
    val genre: String = "Animation"
)
