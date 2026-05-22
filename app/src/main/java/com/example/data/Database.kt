package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val signupTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val name: String,
    val category: String, // "Ecommerce", "Social", "Productivity"
    val description: String,
    val prompt: String,
    val generatedCode: String,
    val hasSync: Boolean,
    val hasStateMgmt: Boolean,
    val refactoredCode: String = "",
    val performanceNotes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE userId = :userId ORDER BY timestamp DESC")
    fun getProjectsForUser(userId: Long): Flow<List<Project>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: Long)
}

@Database(entities = [User::class, Project::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun projectDao(): ProjectDao
}
