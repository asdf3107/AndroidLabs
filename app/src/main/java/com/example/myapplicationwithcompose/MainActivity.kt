package com.example.myapplicationwithcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.Brush

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(
                        onLoginSuccess = {
                            val intent = android.content.Intent(this, SecondActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Круглый логотип с иконкой
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF6200EE), Color(0xFF03DAC5))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Логотип",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Название приложения
//        Text(
//            text = "MyApp",
//            fontSize = 22.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color(0xFF6200EE)
//        )

//        Text(
//            text = "Добро пожаловать",
//            fontSize = 13.sp,
//            color = Color.Gray
//        )
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    // rememberSaveable сохраняет состояние при повороте экрана
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf(false) }
    var passwordError by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var showError by rememberSaveable { mutableStateOf(false) }

    val validCredentials = listOf(
        "user1@mail.com" to "password1",
        "user2@mail.com" to "password2",
        "user3@mail.com" to "password3",
        "user4@mail.com" to "password4",
        "user5@mail.com" to "password5"
    )

    // Определяем ориентацию экрана
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Общий контент формы
    val formContent: @Composable ColumnScope.() -> Unit = {
        // Поле Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
                showError = false
            },
            label = {
                Text(
                    "E-mail",
                    color = if (emailError) Color.Red else Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            isError = emailError,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (emailError) Color.Red else Color.Black,
                unfocusedTextColor = if (emailError) Color.Red else Color.Black,
                errorTextColor = Color.Red,
                focusedBorderColor = if (emailError) Color.Red else Color(0xFF6200EE),
                unfocusedBorderColor = if (emailError) Color.Red else Color.Gray,
            )
        )

        // Поле Пароля
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
                showError = false
            },
            label = {
                Text(
                    "Пароль",
                    color = if (passwordError) Color.Red else Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            isError = passwordError,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = if (passwordError) Color.Red else Color.Black,
                unfocusedTextColor = if (passwordError) Color.Red else Color.Black,
                errorTextColor = Color.Red,
                focusedBorderColor = if (passwordError) Color.Red else Color(0xFF6200EE),
                unfocusedBorderColor = if (passwordError) Color.Red else Color.Gray,
            )
        )

        // Кнопка входа
        Button(
            onClick = {
                emailError = email.isEmpty()
                passwordError = password.isEmpty()

                if (email.isEmpty() || password.isEmpty()) {
                    errorMessage = "Заполните все поля"
                    showError = true
                    return@Button
                }

                val isValid = validCredentials.any { (validEmail, validPassword) ->
                    email == validEmail && password == validPassword
                }

                if (isValid) {
                    onLoginSuccess()
                } else {
                    emailError = true
                    passwordError = true
                    errorMessage = "Неверный E-mail или пароль"
                    showError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6200EE)
            )
        ) {
            Text(
                text = "Войти",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Сообщение об ошибке
        if (showError) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp
            )
        }
    }

    if (isLandscape) {
        // Горизонтальная ориентация: логотип слева, форма справа


            // Форма справа
            Column(
//                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.Center,

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppLogo()

//                Text(
//                    text = "Вход в приложение",
//                    fontSize = 22.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black,
//                    modifier = Modifier.padding(bottom = 16.dp)
//                )
                formContent()
            }
    } else {
        // Вертикальная ориентация: логотип сверху, форма снизу
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Логотип сверху
            AppLogo()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Вход в приложение",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            formContent()
        }
    }
}

@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC5),
            tertiary = Color(0xFF3700B3)
        ),
        content = content
    )
}