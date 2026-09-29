package io.github.chandu4221.designode.domain.port

import io.github.chandu4221.designode.domain.model.NodeId

fun interface NodeIdGenerator {
    fun next(): NodeId
}

object RandomNodeIdGenerator : NodeIdGenerator {
    override fun next(): NodeId = NodeId.generate()
}

class SequentialNodeIdGenerator(private val prefix: String = "test-") : NodeIdGenerator {
    private var counter = 0
    override fun next(): NodeId = NodeId("$prefix${counter++}")
}