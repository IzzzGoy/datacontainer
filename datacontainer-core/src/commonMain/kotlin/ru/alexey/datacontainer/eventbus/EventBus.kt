package ru.alexey.datacontainer.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow


open class EventBus(
    private val innerFlow: MutableSharedFlow<Event>
): SharedFlow<Event> by innerFlow {
    companion object {
        val default: EventBus by lazy {
            EventBus(MutableSharedFlow())
        }
    }

    suspend fun emit(event: Event) { innerFlow.emit(event) }
}
