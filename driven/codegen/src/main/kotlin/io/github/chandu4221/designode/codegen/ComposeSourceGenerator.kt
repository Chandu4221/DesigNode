package io.github.chandu4221.designode.codegen

import io.github.chandu4221.designode.domain.model.AtomicNode
import io.github.chandu4221.designode.domain.service.CodeGenerator

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