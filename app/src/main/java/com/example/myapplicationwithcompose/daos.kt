package com.example.myapplicationwithcompose

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE login = :login AND password = :password")
    suspend fun authenticate(login: String, password: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    suspend fun getAll(): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<MovieEntity>)

    @Query("DELETE FROM movies")
    suspend fun deleteAll()
}
