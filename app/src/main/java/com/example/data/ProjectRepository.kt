package com.example.data

import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {

    fun getProjectsForUser(userId: Long): Flow<List<Project>> {
        return projectDao.getProjectsForUser(userId)
    }

    suspend fun saveProject(project: Project): Long {
        return projectDao.insertProject(project)
    }

    suspend fun deleteProject(id: Long) {
        projectDao.deleteProject(id)
    }
}
