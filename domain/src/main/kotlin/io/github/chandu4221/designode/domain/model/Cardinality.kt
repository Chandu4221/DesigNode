package io.github.chandu4221.designode.domain.model

sealed interface Cardinality {
    data object ZeroOrOne : Cardinality
    data object ExactlyOne : Cardinality
    data object ZeroOrMany : Cardinality
    data object OneOrMany : Cardinality
    data class Range(val min: Int, val max: Int) : Cardinality {
        init {
            require(min >= 0) { "min must be >= 0" }
            require(max >= min) { "max must be >= min, got min=$min, max=$max" }
        }
    }
}