package io.github.chandu4221.designode.ui.canvas

import io.github.chandu4221.designode.ui.canvas.renderers.atomRenderers
import io.github.chandu4221.designode.ui.canvas.renderers.layoutRenderers

/**
 * Assembles the full preview renderer map. As more renderers are added,
 * they're chained here.
 */
object PreviewRenderersRegistry {

    fun build(): PreviewRenderers = PreviewRenderers(
        atomRenderers() + layoutRenderers()
    )
}