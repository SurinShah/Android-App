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
import androidx.compose.material3.TextFieldDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val fullName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val fullNameWasFocused = remember { mutableStateOf(false) }
    val fullNameHasFocus = remember { mutableStateOf(false) }

    val emailWasFocused = remember { mutableStateOf(false) }
    val emailHasFocus = remember { mutableStateOf(false) }

    val passwordWasFocused = remember { mutableStateOf(false) }
    val passwordHasFocus = remember { mutableStateOf(false) }

    val isLoading by authViewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var emailErrorMessage by remember { mutableStateOf<String?>(null) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = if (primaryColor.luminance() > 0.5f) Color.Black else Color.White
    val darkBlue = Color(0xFF2C3E66)

    val inputFieldColors = TextFieldDefaults.colors(
        focusedTextColor = darkBlue,
        unfocusedTextColor = darkBlue,
        focusedLabelColor = darkBlue,
        unfocusedLabelColor = darkBlue,
        focusedIndicatorColor = darkBlue,
        unfocusedIndicatorColor = darkBlue,
        cursorColor = darkBlue,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register", color = textColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor
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
                    value = fullName.value,
                    onValueChange = { fullName.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            fullNameHasFocus.value = it.isFocused
                            if (it.isFocused) fullNameWasFocused.value = true
                        },
                    label = { Text("Enter full name") },
                    singleLine = true,
                    isError = fullNameWasFocused.value && !fullNameHasFocus.value && fullName.value.isBlank(),
                    colors = inputFieldColors
                )
                if (fullNameWasFocused.value && !fullNameHasFocus.value && fullName.value.isBlank()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("Full name cannot be empty", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email.value,
                    onValueChange = {
                        email.value = it
                        emailErrorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            emailHasFocus.value = it.isFocused
                            if (it.isFocused) emailWasFocused.value = true
                        },
                    label = { Text("Enter email") },
                    singleLine = true,
                    isError = ((emailWasFocused.value && !emailHasFocus.value &&
                            (email.value.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email.value).matches()))
                            || emailErrorMessage != null),
                    colors = inputFieldColors
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (emailWasFocused.value && !emailHasFocus.value) {
                        when {
                            email.value.isBlank() -> Text("Email cannot be empty", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            !Patterns.EMAIL_ADDRESS.matcher(email.value).matches() -> Text("Invalid email format", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }
                    }
                    if (emailErrorMessage != null) {
                        Text(emailErrorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
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
                            if (it.isFocused) passwordWasFocused.value = true
                        },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordWasFocused.value && !passwordHasFocus.value && password.value.isBlank(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    colors = inputFieldColors
                )
                if (passwordWasFocused.value && !passwordHasFocus.value && password.value.isBlank()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("Password cannot be empty", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        fullNameWasFocused.value = true
                        emailWasFocused.value = true
                        passwordWasFocused.value = true
                        emailErrorMessage = null

                        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email.value).matches()

                        if (
                            fullName.value.isNotBlank()
                            && email.value.isNotBlank()
                            && isEmailValid
                            && password.value.isNotBlank()
                        ) {
                            authViewModel.register(
                                fullName = fullName.value,
                                email = email.value,
                                password = password.value,
                                onSuccess = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Registered successfully")
                                    }
                                    authViewModel.checkAuthStatus()
                                    navController.navigate("home") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onError = { error ->
                                    emailErrorMessage = if (
                                        error.contains("email", ignoreCase = true) &&
                                        error.contains("exist", ignoreCase = true)
                                    ) {
                                        "Email already exists"
                                    } else {
                                        "Registration failed"
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.large,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (MaterialTheme.colorScheme.background.luminance() > 0.5f)
                            darkBlue else primaryColor
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Register", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val loginText = buildAnnotatedString {
                    append("Already have an account? ")
                    pushStringAnnotation(tag = "LOGIN", annotation = "login")
                    withStyle(
                        style = SpanStyle(
                            color = primaryColor,
                            fontWeight = FontWeight.Medium,
                            textDecoration = TextDecoration.None
                        )
                    ) {
                        append("Login")
                    }
                    pop()
                }

                ClickableText(
                    text = loginText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (MaterialTheme.colorScheme.background.luminance() > 0.5f)
                            Color.Black else Color.White
                    ),
                    onClick = { offset ->
                        loginText.getStringAnnotations("LOGIN", offset, offset).firstOrNull()
                            ?.let { navController.navigate("login") }
                    }
                )
            }
        }
    }
}
