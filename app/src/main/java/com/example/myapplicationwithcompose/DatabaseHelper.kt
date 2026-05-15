package com.example.myapplicationwithcompose

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseHelper {
    fun initializeDatabase(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            
            // Initialize users if empty
            if (db.userDao().count() == 0) {
                val users = listOf(
                    User(login = "user1@mail.com", password = "password1"),
                    User(login = "user2@mail.com", password = "password2"),
                    User(login = "user3@mail.com", password = "password3"),
                    User(login = "user4@mail.com", password = "password4"),
                    User(login = "user5@mail.com", password = "password5")
                )
                db.userDao().insertAll(users)
            }
        }
    }
}
