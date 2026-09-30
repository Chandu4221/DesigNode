package io.github.chandu4221.designode.domain.port

import io.github.chandu4221.designode.domain.event.NodeEvent

/**
 * Publishes node-level change events.
 *
 * Separate from [ProjectEventPublisher] so subscribers that only care about
 * one stream don't have to implement the other.
 */
fun interface NodeEventPublisher {
    fun publish(event: NodeEvent)
}