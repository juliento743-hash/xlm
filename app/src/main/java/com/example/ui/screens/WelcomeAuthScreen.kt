package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel

@Composable
fun WelcomeAuthScreen(
    viewModel: MainViewModel,
    onAuthSuccess: () -> Unit
) {
    var isLoginSelected by remember { mutableStateOf(true) }
    
    // Auth parameters
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    // Additional parameters for Sign Up
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val authResultMsg by viewModel.authStateMessage.collectAsStateWithLifecycle()

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onAuthSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Visual Logo Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "BASE59",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                "build apps with ai in minutes, gratis y más funcional",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Selector Tabs
            TabRow(
                selectedTabIndex = if (isLoginSelected) 0 else 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.Transparent)
            ) {
                Tab(
                    selected = isLoginSelected,
                    onClick = { isLoginSelected = true },
                    modifier = Modifier.testTag("tab_login")
                ) {
                    Text("Iniciar Sesión", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Tab(
                    selected = !isLoginSelected,
                    onClick = { isLoginSelected = false },
                    modifier = Modifier.testTag("tab_register")
                ) {
                    Text("Registrarse", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reactive Forms with animated presentation elements
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Username field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario") },
                    leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (!isLoginSelected) {
                    // Full Name field - sign up only
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nombre Completo") },
                        leadingIcon = { Icon(Icons.Default.Face, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("fullname_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Email field - sign up only
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action submit button with 48.dp target size
            Button(
                onClick = {
                    if (isLoginSelected) {
                        viewModel.login(username, password)
                    } else {
                        viewModel.signUp(username, email, password, fullName)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("auth_submit_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (isLoginSelected) "Acceder al Compilador" else "Registrar Cuenta",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Warning dialog or messages
            authResultMsg?.let { msg ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (msg.contains("success", ignoreCase = true) || msg.contains("successfully", ignoreCase = true) || msg.contains("Welcome", ignoreCase = true)) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (msg.contains("success", ignoreCase = true) || msg.contains("Welcome", ignoreCase = true)) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (msg.contains("success", ignoreCase = true) || msg.contains("Welcome", ignoreCase = true)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = msg,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (msg.contains("success", ignoreCase = true) || msg.contains("Welcome", ignoreCase = true)) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}
