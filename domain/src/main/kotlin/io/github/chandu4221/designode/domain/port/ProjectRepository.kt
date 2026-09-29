package io.github.chandu4221.designode.domain.port

import io.github.chandu4221.designode.domain.model.Project
import io.github.chandu4221.designode.domain.model.ProjectId

interface ProjectRepository {
    suspend fun save(project: Project): Result<Unit>
    suspend fun load(id: ProjectId): Result<Project>
    suspend fun list(): Result<List<ProjectSummary>>
    suspend fun delete(id: ProjectId): Result<Unit>
}

data class ProjectSummary(
    val id: ProjectId,
    val name: String,
    val screenCount: Int,
)