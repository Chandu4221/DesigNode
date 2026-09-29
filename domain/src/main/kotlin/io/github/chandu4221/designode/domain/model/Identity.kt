package io.github.chandu4221.designode.domain.model

import java.util.*

@JvmInline
value class ProjectId(val value: String) {
    init { require(value.isNotBlank()) }
    companion object {
        fun generate() = ProjectId(java.util.UUID.randomUUID().toString())
    }
}

@JvmInline
value class ScreenId(val value: String) {
    init { require(value.isNotBlank()) }
    companion object {
        fun generate() = ScreenId(java.util.UUID.randomUUID().toString())
    }
}


@JvmInline
value class NodeId(val value: String) {
    init {
        require(value.isNotBlank()) { "NodeId cannot be blank" }
    }

    companion object {
        fun generate(): NodeId {
            return NodeId(UUID.randomUUID().toString())
        }
    }
}

@JvmInline
value class SlotId(val value: String) {
    init {
        require(value.isNotBlank()) { "SlotId cannot be blank" }
    }
}

@JvmInline
value class ComponentTypeId(val value: String) {
    init {
        require(value.isNotBlank()) { "ComponentTypeId cannot be blank" }
    }
}

@JvmInline
value class PropertyKey(val value: String) {
    init {
        require(value.isNotBlank()) { "PropertyKey cannot be blank" }
    }
}

@JvmInline
value class VariantId(val value: String) {
    init {
        require(value.isNotBlank()) { "VariantId cannot be blank" }
    }
}

@JvmInline
value class FamilyId(val value: String) {
    init {
        require(value.isNotBlank()) { "FamilyId cannot be blank" }
    }
}