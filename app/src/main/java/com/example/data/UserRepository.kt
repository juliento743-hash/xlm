package com.example.data

import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(username: String, email: String, password: plainTextPassword, fullName: String): RegisterResult {
        if (userDao.getUserByUsername(username) != null) {
            return RegisterResult.Error("Username already exists")
        }
        if (userDao.getUserByEmail(email) != null) {
            return RegisterResult.Error("Email already registered")
        }

        val hashedPassword = hashPassword(password)
        val newUser = User(
            username = username,
            email = email,
            passwordHash = hashedPassword,
            fullName = fullName
        )
        val id = userDao.insertUser(newUser)
        return RegisterResult.Success(newUser.copy(id = id))
    }

    suspend fun loginUser(username: String, password: plainTextPassword): LoginResult {
        val user = userDao.getUserByUsername(username) ?: return LoginResult.Error("User not found")
        val hashedPassword = hashPassword(password)
        return if (user.passwordHash == hashedPassword) {
            LoginResult.Success(user)
        } else {
            LoginResult.Error("Incorrect password")
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}

typealias plainTextPassword = String

sealed class RegisterResult {
    data class Success(val user: User) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}
