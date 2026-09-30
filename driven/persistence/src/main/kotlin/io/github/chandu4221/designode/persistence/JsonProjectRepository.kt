package io.github.chandu4221.designode.persistence

import io.github.chandu4221.designode.domain.model.Project
import io.github.chandu4221.designode.domain.model.ProjectId
import io.github.chandu4221.designode.domain.port.ProjectRepository
import io.github.chandu4221.designode.domain.port.ProjectSummary
import io.github.chandu4221.designode.persistence.dto.ProjectDto
import io.github.chandu4221.designode.persistence.mapper.toDomain
import io.github.chandu4221.designode.persistence.mapper.toDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readText
import kotlin.io.path.writeText

/**
 * File-backed [ProjectRepository]. One JSON file per project, named
 * `<projectId>.json`, stored under [baseDir].
 *
 * Every operation runs on [Dispatchers.IO]. Failures — I/O errors, decode
 * errors, blank IDs — are wrapped in [Result.failure] rather than thrown.
 */
class JsonProjectRepository(
    private val baseDir: Path,
) : ProjectRepository {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = false
        ignoreUnknownKeys = true
    }

    init {
        baseDir.createDirectories()
    }

    override suspend fun save(project: Project): Result<Unit> = io {
        val file = fileFor(project.id)
        val dto = project.toDto()
        file.writeText(json.encodeToString(ProjectDto.serializer(), dto))
    }

    override suspend fun load(id: ProjectId): Result<Project> = io {
        val file = fileFor(id)
        if (!file.exists()) error("Project not found: ${id.value}")
        val dto = json.decodeFromString(ProjectDto.serializer(), file.readText())
        dto.toDomain()
    }

    override suspend fun list(): Result<List<ProjectSummary>> = io {
        baseDir.listDirectoryEntries("*.json")
            .mapNotNull { path ->
                runCatching {
                    val dto = json.decodeFromString(ProjectDto.serializer(), path.readText())
                    val project = dto.toDomain()
                    ProjectSummary(
                        id = project.id,
                        name = project.name,
                        screenCount = project.screens.size,
                    )
                }.getOrNull()
            }
            .sortedBy { it.name }
    }

    override suspend fun delete(id: ProjectId): Result<Unit> = io {
        fileFor(id).deleteIfExists()
        Unit
    }

    private fun fileFor(id: ProjectId): Path = baseDir.resolve("${id.value}.json")

    private suspend fun <T> io(block: () -> T): Result<T> =
        withContext(Dispatchers.IO) { runCatching(block) }
}