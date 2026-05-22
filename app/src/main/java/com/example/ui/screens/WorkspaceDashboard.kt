package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Project
import com.example.ui.MainViewModel
import com.example.ui.SocialPost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceDashboard(
    viewModel: MainViewModel,
    onLogoutClick: () -> Unit
) {
    var activeTab by remember { mutableStateOf(DashboardTab.TEMPLATES) }
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val authMessage by viewModel.authStateMessage.collectAsStateWithLifecycle()
    
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authMessage) {
        authMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearAuthMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BASE59",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    var showProfileDialog by remember { mutableStateOf(false) }
                    IconButton(
                        onClick = { showProfileDialog = true },
                        modifier = Modifier.testTag("profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "Profile Details",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    if (showProfileDialog) {
                        ProfileDetailsDialog(
                            fullName = currentUser?.fullName ?: "Anonymous Developer",
                            email = currentUser?.email ?: "no-email@example.com",
                            username = currentUser?.username ?: "anonymous",
                            signupTimestamp = currentUser?.signupTimestamp ?: System.currentTimeMillis(),
                            onDismiss = { showProfileDialog = false },
                            onLogout = {
                                showProfileDialog = false
                                viewModel.logout()
                                onLogoutClick()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == DashboardTab.TEMPLATES,
                    onClick = { activeTab = DashboardTab.TEMPLATES },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Templates") },
                    label = { Text("Plantillas") },
                    modifier = Modifier.testTag("tab_templates")
                )
                NavigationBarItem(
                    selected = activeTab == DashboardTab.AI_GENERATOR,
                    onClick = { activeTab = DashboardTab.AI_GENERATOR },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Companion") },
                    label = { Text("IA Creador") },
                    modifier = Modifier.testTag("tab_ai_generator")
                )
                NavigationBarItem(
                    selected = activeTab == DashboardTab.MY_WORKSPACE,
                    onClick = { activeTab = DashboardTab.MY_WORKSPACE },
                    icon = { Icon(Icons.Default.Folder, contentDescription = "Workspace") },
                    label = { Text("Mi Espacio") },
                    modifier = Modifier.testTag("tab_workspace")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                DashboardTab.TEMPLATES -> TemplatesTabContent(viewModel)
                DashboardTab.AI_GENERATOR -> AiGeneratorTabContent(viewModel)
                DashboardTab.MY_WORKSPACE -> WorkspaceTabContent(viewModel)
            }
        }
    }
}

enum class DashboardTab {
    TEMPLATES, AI_GENERATOR, MY_WORKSPACE
}

@Composable
fun ProfileDetailsDialog(
    fullName: String,
    email: String,
    username: String,
    signupTimestamp: Long,
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "@$username",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Correo:", fontWeight = FontWeight.Medium)
                    Text(email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Registro:", fontWeight = FontWeight.Medium)
                    Text("Base59 Cloud Sync", color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("logout_confirm_button")
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// ---------------- TEMPLATES TAB CONTENT ----------------

@Composable
fun TemplatesTabContent(viewModel: MainViewModel) {
    var selectedTemplateCategory by remember { mutableStateOf<String?>(null) }
    
    // Dynamic styling variables configured by the user inside the preview
    var customTitle by remember { mutableStateOf("") }
    var selectedAccentColorName by remember { mutableStateOf("Esmeralda") }
    var syncSpeedSeconds by remember { mutableStateOf(3f) }

    val accentColor = when (selectedAccentColorName) {
        "Esmeralda" -> Color(0xFF00BFA5)
        "Amatista" -> Color(0xFF8E24AA)
        "Coral" -> Color(0xFFFF5252)
        "Ámbar" -> Color(0xFFFFC400)
        else -> MaterialTheme.colorScheme.primary
    }

    if (selectedTemplateCategory == null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Biblioteca de Plantillas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Selecciona un punto de partida funcional para lanzar el simulador inteligente, personalizar las propiedades interactivas y exportar código.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            item {
                Text(
                    "Categorías Destacadas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // template 1: E-commerce
            item {
                TemplateCard(
                    title = "Plataforma de E-Commerce",
                    category = "Comercio Electrónico",
                    description = "Catálogo dinámico intermedio con sumador inteligente de carrito de compras, cálculo de impuestos, checkout realista y sincronización de stock.",
                    icon = Icons.Default.ShoppingCart,
                    onClick = {
                        selectedTemplateCategory = "ecommerce"
                        customTitle = "Base59 Market"
                    }
                )
            }

            // template 2: Social media
            item {
                TemplateCard(
                    title = "Red Social Activa",
                    category = "Redes Sociales",
                    description = "Muro de publicaciones reactivas con contador dinámico de likes ('me gusta'), subproceso de comentarios en tiempo real y flujo de estado.",
                    icon = Icons.Default.Public,
                    onClick = {
                        selectedTemplateCategory = "social"
                        customTitle = "Social Flow"
                    }
                )
            }

            // template 3: Kanban Productivity
            item {
                TemplateCard(
                    title = "Tablero Kanban de Productividad",
                    category = "Productividad",
                    description = "Clásico organizador visual con tres carriles (Por Hacer, En Proceso, Hecho). Transferencias con clics responsivos, adición de tareas y borrado.",
                    icon = Icons.Default.AssignmentTurnedIn,
                    onClick = {
                        selectedTemplateCategory = "productivity"
                        customTitle = "Kanban Board"
                    }
                )
            }
        }
    } else {
        // Active Simulator View!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header showing customized variables
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedTemplateCategory = null }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    "Simulador Interactivo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Badge(containerColor = accentColor) {
                    Text("LIVE COMPILING", modifier = Modifier.padding(4.dp), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Properties / Control panel to customize simulator
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Ajustes del Compilador Gráfico", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Edit Title Property
                    OutlinedTextField(
                        value = customTitle,
                        onValueChange = { customTitle = it },
                        label = { Text("Título de la Aplicación") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Color Accent Option picker
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Color de Acento:", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Esmeralda", "Amatista", "Coral", "Ámbar").forEach { colorName ->
                                    val chipColor = when (colorName) {
                                        "Esmeralda" -> Color(0xFF00BFA5)
                                        "Amatista" -> Color(0xFF8E24AA)
                                        "Coral" -> Color(0xFFFF5252)
                                        "Ámbar" -> Color(0xFFFFC400)
                                        else -> Color.Gray
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(chipColor)
                                            .border(
                                                width = if (selectedAccentColorName == colorName) 2.dp else 0.dp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .clickable { selectedAccentColorName = colorName }
                                    )
                                }
                            }
                        }

                        // Sync Interval slide property
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Reguladores de Sincronización (${syncSpeedSeconds.toInt()}s):", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Slider(
                                value = syncSpeedSeconds,
                                onValueChange = { syncSpeedSeconds = it },
                                valueRange = 1f..10f,
                                steps = 9
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Liquid Simulator Box Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, accentColor, RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (selectedTemplateCategory) {
                    "ecommerce" -> EcommerceSimulator(customTitle, accentColor)
                    "social" -> SocialSimulator(customTitle, accentColor, viewModel)
                    "productivity" -> ProductivitySimulator(customTitle, accentColor, viewModel)
                }
            }
        }
    }
}

@Composable
fun TemplateCard(
    title: String,
    category: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                SuggestionChip(
                    onClick = {},
                    label = { Text(category, fontSize = 10.sp) },
                    modifier = Modifier.height(20.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ---------------- INDIVIDUAL LIVE SIMULATORS ----------------

@Composable
fun EcommerceSimulator(title: String, accentColor: Color) {
    var cartItemsCount by remember { mutableStateOf(0) }
    var totalPrice by remember { mutableStateOf(0.0) }
    var showCheckoutDialog by remember { mutableStateOf(false) }

    val products = listOf(
        Pair("Remera Inteligente Base59", 25.00),
        Pair("Notebook Gamer AI Pro", 850.00),
        Pair("Abono Gemini Code Plus", 15.00)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // App header inside simulation container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(accentColor)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title.uppercase(),
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 14.sp
            )
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)
                if (cartItemsCount > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-4).dp)
                    ) {
                        Text(cartItemsCount.toString(), color = Color.White, fontSize = 9.sp)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(product.first, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("$${product.second}", color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = {
                                cartItemsCount++
                                totalPrice += product.second
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Añadir", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Checkout bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Estimado:", fontSize = 11.sp)
                    Text("$${String.format("%.2f", totalPrice)} USD", fontWeight = FontWeight.Black, fontSize = 15.sp, color = accentColor)
                }
                Button(
                    onClick = { if (cartItemsCount > 0) showCheckoutDialog = true },
                    enabled = cartItemsCount > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Simular Compra", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }

    if (showCheckoutDialog) {
        Dialog(onDismissRequest = { showCheckoutDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = accentColor, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("¡Proceso de Compra Exitoso!", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Sincronización de transacción completa con la base de datos de Base59 Cloud.", fontSize = 12.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            showCheckoutDialog = false
                            cartItemsCount = 0
                            totalPrice = 0.0
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        Text("Resetear Simulación", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun SocialSimulator(title: String, accentColor: Color, viewModel: MainViewModel) {
    val posts by viewModel.socialPosts.collectAsStateWithLifecycle()
    var newPostText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(accentColor)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title.uppercase(),
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 14.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sync Live", color = Color.White, fontSize = 11.sp)
            }
        }

        // Post creator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newPostText,
                onValueChange = { newPostText = it },
                placeholder = { Text("Escribe algo para publicar...", fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                maxLines = 2,
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Button(
                onClick = {
                    if (newPostText.isNotBlank()) {
                        viewModel.addCommentToPost(1, newPostText) // simulates activity
                        newPostText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Post", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(posts) { post ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = accentColor, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(post.author, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(post.text, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.clickable { viewModel.likePost(post.id) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${post.likes} Likes", fontSize = 11.sp)
                            }
                            Text("${post.comments.size} comentarios", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductivitySimulator(title: String, accentColor: Color, viewModel: MainViewModel) {
    val todo by viewModel.todoTasks.collectAsStateWithLifecycle()
    val inProgress by viewModel.inProgressTasks.collectAsStateWithLifecycle()
    val done by viewModel.doneTasks.collectAsStateWithLifecycle()

    var taskInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(accentColor)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title.uppercase(),
                fontWeight = FontWeight.Black,
                color = Color.White,
                fontSize = 14.sp
            )
            Icon(Icons.Default.CloudQueue, contentDescription = null, tint = Color.White)
        }

        // Add task input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = taskInput,
                onValueChange = { taskInput = it },
                placeholder = { Text("Nueva tarea...", fontSize = 11.sp) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Button(
                onClick = {
                    if (taskInput.isNotBlank()) {
                        todo.add(taskInput)
                        taskInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                contentPadding = PaddingValues(horizontal = 10.dp),
                modifier = Modifier.height(45.dp)
            ) {
                Text("Crear", fontSize = 11.sp, color = Color.White)
            }
        }

        // Swimlanes
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Lane 1
            SwimlaneColumn("Por Hacer", todo, modifier = Modifier.weight(1f), titleColor = accentColor) { item ->
                todo.remove(item)
                inProgress.add(item)
            }
            // Lane 2
            SwimlaneColumn("En Proceso", inProgress, modifier = Modifier.weight(1f), titleColor = accentColor) { item ->
                inProgress.remove(item)
                done.add(item)
            }
            // Lane 3
            SwimlaneColumn("Hecho", done, modifier = Modifier.weight(1f), titleColor = accentColor) { item ->
                done.remove(item)
            }
        }
    }
}

@Composable
fun SwimlaneColumn(
    title: String,
    tasks: MutableList<String>,
    modifier: Modifier,
    titleColor: Color,
    onMove: (String) -> Unit
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.2f))
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = titleColor,
                modifier = Modifier.padding(4.dp)
            )
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(titleColor)
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(tasks) { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMove(task) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(task, fontSize = 9.sp, lineHeight = 11.sp)
                            Icon(Icons.Default.ChevronRight, contentDescription = "Move", modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
        }
    }
}


// ---------------- GRAPHIC AI CODE GENERATOR TAB CONTENT ----------------

@Composable
fun AiGeneratorTabContent(viewModel: MainViewModel) {
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val generateResult by viewModel.generateResult.collectAsStateWithLifecycle()
    val error by viewModel.generationError.collectAsStateWithLifecycle()

    var userPrompt by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Productivity") }
    var syncSwitch by remember { mutableStateOf(true) }
    var complexStateSwitch by remember { mutableStateOf(true) }

    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Autómata Creador de IA",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Genera vistas complejas, sincronización bidireccional, estructuras de estado robustas y auditoría de rendimiento en segundos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Describe tu Aplicación:", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = userPrompt,
                    onValueChange = { userPrompt = it },
                    placeholder = { Text("Ej: Tablero de Delivery con chat interconectado de repartidores y alertas directas...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .testTag("ai_prompt_input")
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Categoría Organizadora:", fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Productivity", "Ecommerce", "Social").forEach { category ->
                        val spanishName = when (category) {
                            "Productivity" -> "Productividad"
                            "Ecommerce" -> "Comercio Electrónico"
                            else -> "Red Social"
                        }
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(spanishName) }
                        )
                    }
                }
            }
        }

        // Advanced AI Features Toggles
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.3f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Configuraciones de IA Avanzadas", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sync, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sincronización en Tiempo Real", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Text("Generar buffers websockets simulados y triggers reactivos automáticos.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = syncSwitch,
                            onCheckedChange = { syncSwitch = it },
                            modifier = Modifier.testTag("sync_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Layers, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gestión de Estado Compleja", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Text("Modelar la UI usando StateFlow, cargas seguras MVI y bloqueo reentrante.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = complexStateSwitch,
                            onCheckedChange = { complexStateSwitch = it },
                            modifier = Modifier.testTag("state_management_switch")
                        )
                    }
                }
            }
        }

        item {
            if (isGenerating) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("IA Base59 está diseñando y auditando tu código...", fontWeight = FontWeight.Bold)
                    Text("Inyectando patrones asíncronos y calibrando frames", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            } else {
                Button(
                    onClick = {
                        viewModel.generateAppWithAI(userPrompt, selectedCategory, syncSwitch, complexStateSwitch)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("generate_ui_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compilar con Inteligencia Artificial")
                }
            }
        }

        error?.let {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(it, modifier = Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }

        generateResult?.let { result ->
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Estructura de la Aplicación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(result.name, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                    }
                    Button(
                        onClick = { viewModel.saveGeneratedProjectToWorkspace() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BFA5))
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Guardar en Mi Espacio", fontSize = 11.sp)
                    }
                }
            }

            item {
                var selectedCodeSubTab by remember { mutableStateOf(0) }
                Column {
                    TabRow(selectedTabIndex = selectedCodeSubTab) {
                        Tab(selected = selectedCodeSubTab == 0, onClick = { selectedCodeSubTab = 0 }) {
                            Text("Código Composable", modifier = Modifier.padding(10.dp), fontSize = 12.sp)
                        }
                        Tab(selected = selectedCodeSubTab == 1, onClick = { selectedCodeSubTab = 1 }) {
                            Text("Refactorizado Limpio", modifier = Modifier.padding(10.dp), fontSize = 12.sp)
                        }
                        Tab(selected = selectedCodeSubTab == 2, onClick = { selectedCodeSubTab = 2 }) {
                            Text("Rendimiento", modifier = Modifier.padding(10.dp), fontSize = 12.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.7f))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    ) {
                        val activeTextToShow = when (selectedCodeSubTab) {
                            0 -> result.code
                            1 -> result.refactoredCode
                            else -> result.performanceNotes
                        }

                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { clipboardManager.setText(AnnotatedString(activeTextToShow)) }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copiar Código", fontSize = 11.sp)
                                }
                            }
                            Text(
                                text = activeTextToShow,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------- USER WORKSPACE / RECORDS TAB CONTENT ----------------

@Composable
fun WorkspaceTabContent(viewModel: MainViewModel) {
    val projects by viewModel.userProjects.collectAsStateWithLifecycle(initialValue = emptyList())
    var activeViewerProject by remember { mutableStateOf<Project?>(null) }
    val clipboardManager = LocalClipboardManager.current

    if (activeViewerProject == null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Mi Espacio de Trabajo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Historial permanente de creaciones compiladas guardadas de forma segura en Room DB.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (projects.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Source,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Sin Proyectos Guardados",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Los códigos que compiles utilizando el Autómata Creador de IA aparecerán aquí para sincronizarse libremente.",
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(projects) { project ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activeViewerProject = project }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = project.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                IconButton(onClick = { viewModel.deleteProject(project.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Categoría: ${project.category}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = project.description,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    } else {
        // Individual saved project detail view
        val project = activeViewerProject!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { activeViewerProject = null }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    "Visor Integrador",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = {
                    viewModel.deleteProject(project.id)
                    activeViewerProject = null
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = project.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Cargado en Room de forma segura",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            var selectedSubTabViewer by remember { mutableStateOf(0) }
            Column(modifier = Modifier.weight(1f)) {
                TabRow(selectedTabIndex = selectedSubTabViewer) {
                    Tab(selected = selectedSubTabViewer == 0, onClick = { selectedSubTabViewer = 0 }) {
                        Text("Vistas", modifier = Modifier.padding(8.dp), fontSize = 11.sp)
                    }
                    Tab(selected = selectedSubTabViewer == 1, onClick = { selectedSubTabViewer = 1 }) {
                        Text("Optimizado", modifier = Modifier.padding(8.dp), fontSize = 11.sp)
                    }
                    Tab(selected = selectedSubTabViewer == 2, onClick = { selectedSubTabViewer = 2 }) {
                        Text("Diagnóstico", modifier = Modifier.padding(8.dp), fontSize = 11.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.7f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                ) {
                    val activeViewerText = when (selectedSubTabViewer) {
                        0 -> project.generatedCode
                        1 -> project.refactoredCode
                        else -> project.performanceNotes
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { clipboardManager.setText(AnnotatedString(activeViewerText)) }) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copiar", fontSize = 11.sp)
                            }
                        }

                        // Scrollable content viewport
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            item {
                                Text(
                                    text = activeViewerText,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
