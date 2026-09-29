package io.github.chandu4221.designode.domain.port

import io.github.chandu4221.designode.domain.event.DomainEvent

interface EventPublisher {
    fun publish(event: DomainEvent)
}