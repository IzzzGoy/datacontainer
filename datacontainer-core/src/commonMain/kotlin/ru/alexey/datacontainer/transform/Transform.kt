package ru.alexey.datacontainer.transform

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class Transform<T : Any, out OUTER>(
    val flow: StateFlow<T>,
    val transform: (T, @UnsafeVariance OUTER) -> OUTER
) {
    operator fun component1(): Flow<T> {
        return flow
    }

    operator fun component2(): (T, @UnsafeVariance OUTER) -> OUTER {
        return transform
    }
}