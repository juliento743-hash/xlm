package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Database initialized securely
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "recrea_base59_db"
        ).fallbackToDestructiveMigration().build()
    }

    private val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }

    private val projectRepository: ProjectRepository by lazy {
        ProjectRepository(database.projectDao())
    }

    // Authentication States
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _authStateMessage = MutableStateFlow<String?>(null)
    val authStateMessage: StateFlow<String?> = _authStateMessage.asStateFlow()

    // Workspace & Projects Flow
    val userProjects: Flow<List<Project>> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            projectRepository.getProjectsForUser(user.id)
        } else {
            flowOf(emptyList())
        }
    }

    // Code Gen States
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generateResult = MutableStateFlow<GeneratedAppResult?>(null)
    val generateResult: StateFlow<GeneratedAppResult?> = _generateResult.asStateFlow()

    private val _generationError = MutableStateFlow<String?>(null)
    val generationError: StateFlow<String?> = _generationError.asStateFlow()

    // Simulator Interactive States
    // 1. Productivity Board State
    val todoTasks = _currentUser.flatMapLatest {
        flowOf(mutableListOf("Create Landing Page", "Integrate Payment Gateway", "Set up unit tests"))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), mutableListOf())

    val inProgressTasks = _currentUser.flatMapLatest {
        flowOf(mutableListOf("Design M3 Layout", "Configure OAuth Scope"))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), mutableListOf())

    val doneTasks = _currentUser.flatMapLatest {
        flowOf(mutableListOf("Initialize Git Repository"))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), mutableListOf())

    // 2. E-Commerce Cart Simulator State
    private val _cartProductIds = MutableStateFlow<Map<Long, Int>>(emptyMap()) // productId -> quantity
    val cartProductIds: StateFlow<Map<Long, Int>> = _cartProductIds.asStateFlow()

    // 3. Social Media feed list state
    private val _socialPosts = MutableStateFlow<List<SocialPost>>(
        listOf(
            SocialPost(1, "Sansa Stark", "Just launched our tech ecosystem on Base59 platform! Built in minutes & fully functional.", 124, mutableListOf("Wow this is blazing fast!", "Espectacular!")),
            SocialPost(2, "AI Assistant", "Welcome to Base59. Toggle 'Real-time sync' to enable continuous cloud replication.", 98, mutableListOf("Nice work", "Love the UI design"))
        )
    )
    val socialPosts: StateFlow<List<SocialPost>> = _socialPosts.asStateFlow()

    fun clearAuthMessage() {
        _authStateMessage.value = null
    }

    fun signUp(username: String, email: String, password: plainTextPassword, fullName: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank() || fullName.isBlank()) {
            _authStateMessage.value = "All fields are required"
            return
        }
        viewModelScope.launch {
            try {
                val result = userRepository.registerUser(username, email, password, fullName)
                when (result) {
                    is RegisterResult.Success -> {
                        _currentUser.value = result.user
                        _authStateMessage.value = "Registered successfully!"
                    }
                    is RegisterResult.Error -> {
                        _authStateMessage.value = result.message
                    }
                }
            } catch (e: Exception) {
                _authStateMessage.value = "Registration failed: ${e.message}"
            }
        }
    }

    fun login(username: String, password: plainTextPassword) {
        if (username.isBlank() || password.isBlank()) {
            _authStateMessage.value = "Please complete all credentials"
            return
        }
        viewModelScope.launch {
            try {
                val result = userRepository.loginUser(username, password)
                when (result) {
                    is LoginResult.Success -> {
                        _currentUser.value = result.user
                        _authStateMessage.value = "Welcome back, ${result.user.fullName}!"
                    }
                    is LoginResult.Error -> {
                        _authStateMessage.value = result.message
                    }
                }
            } catch (e: Exception) {
                _authStateMessage.value = "Login failed: ${e.message}"
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _generateResult.value = null
        _authStateMessage.value = "Successful log out."
    }

    // Project Operations
    fun generateAppWithAI(prompt: String, category: String, enableSync: Boolean, enableStateMgmt: Boolean) {
        if (prompt.isBlank()) {
            _generationError.value = "Please describe the app layout you want to build"
            return
        }
        viewModelScope.launch {
            _isGenerating.value = true
            _generationError.value = null
            _generateResult.value = null
            try {
                val result = CodeGeneratorHelper.generateApp(prompt, category, enableSync, enableStateMgmt)
                _generateResult.value = result
            } catch (e: Exception) {
                _generationError.value = "AI generation failed: ${e.message}"
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun saveGeneratedProjectToWorkspace() {
        val result = _generateResult.value ?: return
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val project = Project(
                userId = user.id,
                name = result.name,
                category = result.category,
                description = result.description,
                prompt = result.code, // saves input/code
                generatedCode = result.code,
                hasSync = result.hasSync,
                hasStateMgmt = result.hasStateMgmt,
                refactoredCode = result.refactoredCode,
                performanceNotes = result.performanceNotes
            )
            projectRepository.saveProject(project)
            _authStateMessage.value = "Project saved to workspace!"
        }
    }

    fun deleteProject(projectId: Long) {
        viewModelScope.launch {
            projectRepository.deleteProject(projectId)
            _authStateMessage.value = "Project deleted successfully"
        }
    }

    // Cart Operations
    fun addProductToCart(productId: Long) {
        val current = _cartProductIds.value.toMutableMap()
        current[productId] = (current[productId] ?: 0) + 1
        _cartProductIds.value = current
    }

    fun removeProductFromCart(productId: Long) {
        val current = _cartProductIds.value.toMutableMap()
        val count = current[productId] ?: 0
        if (count > 1) {
            current[productId] = count - 1
        } else {
            current.remove(productId)
        }
        _cartProductIds.value = current
    }

    fun clearCart() {
        _cartProductIds.value = emptyMap()
    }

    // Social feed interactions
    fun likePost(postId: Int) {
        val list = _socialPosts.value.map {
            if (it.id == postId) {
                it.copy(likes = it.likes + 1)
            } else it
        }
        _socialPosts.value = list
    }

    fun addCommentToPost(postId: Int, commentText: String) {
        if (commentText.isBlank()) return
        val list = _socialPosts.value.map {
            if (it.id == postId) {
                val newComments = it.comments.toMutableList()
                newComments.add(commentText)
                it.copy(comments = newComments)
            } else it
        }
        _socialPosts.value = list
    }
}

data class SocialPost(
    val id: Int,
    val author: String,
    val text: String,
    val likes: Int,
    val comments: List<String>
)
