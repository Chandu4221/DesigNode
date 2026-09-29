package io.github.chandu4221.designode.domain.port

import io.github.chandu4221.designode.domain.event.ProjectEvent

fun interface ProjectEventPublisher {
    fun publish(event: ProjectEvent)
}