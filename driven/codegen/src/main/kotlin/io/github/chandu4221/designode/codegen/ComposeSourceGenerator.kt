package io.github.chandu4221.designode.codegen

import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.service.CodeGenerator

import io.github.chandu4221.designode.domain.model.Project
import io.github.chandu4221.designode.domain.model.Screen

class ComposeSourceGenerator(
    private val generator: CodeGenerator = EmitterRegistry.codeGenerator(),
    private val imports: String = DEFAULT_IMPORTS,
) {

    fun generate(
        root: AtomicNode,
        functionName: String = "Screen",
    ): String {
        val body = generator.generate(root)
        val indentedBody = indentLines(body, "    ")

        return buildString {
            append(imports)
            append("\n\n")
            append("@OptIn(ExperimentalMaterial3Api::class)\n")
            append("@Composable\n")
            append("fun $functionName() {\n")
            append(indentedBody).append("\n")
            append("}")
        }
    }

    fun generate(screens: List<Screen>): String {
        if (screens.isEmpty()) return imports
        return buildString {
            append(imports)
            screens.forEach { screen ->
                val functionName = toComposableFunctionName(screen.name)
                val body = generator.generate(screen.root)
                val indentedBody = indentLines(body, "    ")

                append("\n\n@OptIn(ExperimentalMaterial3Api::class)\n")
                append("@Composable\n")
                append("fun $functionName() {\n")
                append(indentedBody).append("\n")
                append("}")
            }
        }
    }

    fun generate(project: Project): String = generate(project.screens.values.toList())

    private fun toComposableFunctionName(rawName: String): String {
        val words = rawName.split(Regex("[^a-zA-Z0-9]+")).filter { it.isNotBlank() }
        val base = words.joinToString("") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
        val identifier = if (base.isEmpty() || !base.first().isLetter()) "Screen$base" else base
        return if (identifier.endsWith("Screen")) identifier else "${identifier}Screen"
    }

    companion object {
        val DEFAULT_IMPORTS: String = """
            import androidx.compose.foundation.layout.*
            import androidx.compose.material.icons.Icons
            import androidx.compose.material.icons.filled.*
            import androidx.compose.material3.*
            import androidx.compose.material3.ExperimentalMaterial3Api
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.Alignment
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.graphics.Color
            import androidx.compose.ui.text.style.TextAlign
            import androidx.compose.ui.unit.dp
        """.trimIndent()
    }
}