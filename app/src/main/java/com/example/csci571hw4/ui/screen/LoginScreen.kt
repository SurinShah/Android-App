package com.example.csci571hw4.ui.screen

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.csci571hw4.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val emailWasFocused = remember { mutableStateOf(false) }
    val emailHasFocus = remember { mutableStateOf(false) }

    val passwordWasFocused = remember { mutableStateOf(false) }
    val passwordHasFocus = remember { mutableStateOf(false) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val loginSuccess by authViewModel.loginSuccess.collectAsState()
    val loginError by authViewModel.loginError.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val isLightTheme = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val lightBlue = Color(0xFFE3ECFA)
    val darkBlue = Color(0xFF2C3E66)

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Logged in successfully")
            }
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
            authViewModel.resetLoginState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Login", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isLightTheme) lightBlue else MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            emailHasFocus.value = it.isFocused
                            if (it.isFocused) {
                                emailWasFocused.value = true
                            }
                        },
                    label = { Text("Email") },
                    singleLine = true,
                    isError = emailWasFocused.value && !emailHasFocus.value &&
                            (email.value.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email.value).matches()),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                )

                if (emailWasFocused.value && !emailHasFocus.value) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        when {
                            email.value.isBlank() -> Text(
                                "Email cannot be empty",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                            !Patterns.EMAIL_ADDRESS.matcher(email.value).matches() -> Text(
                                "Invalid email format",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            passwordHasFocus.value = it.isFocused
                            if (it.isFocused) {
                                passwordWasFocused.value = true
                            }
                        },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = passwordWasFocused.value && !passwordHasFocus.value && password.value.isBlank(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )

                if (passwordWasFocused.value && !passwordHasFocus.value && password.value.isBlank()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Password cannot be empty",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        emailWasFocused.value = true
                        passwordWasFocused.value = true

                        if (email.value.isNotBlank()
                            && Patterns.EMAIL_ADDRESS.matcher(email.value).matches()
                            && password.value.isNotBlank()
                        ) {
                            authViewModel.login(email.value, password.value)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.large,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkBlue,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Login", color = Color.White)
                    }
                }

                loginError?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val registerText = buildAnnotatedString {
                    append("Don't have an account yet? ")
                    pushStringAnnotation(tag = "REGISTER", annotation = "register")
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            textDecoration = TextDecoration.None
                        )
                    ) {
                        append("Register")
                    }
                    pop()
                }

                ClickableText(
                    text = registerText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isLightTheme) Color.Black else Color.White
                    ),
                    onClick = { offset ->
                        registerText.getStringAnnotations("REGISTER", offset, offset).firstOrNull()
                            ?.let { navController.navigate("register") }
                    }
                )
            }
        }
    }
}
