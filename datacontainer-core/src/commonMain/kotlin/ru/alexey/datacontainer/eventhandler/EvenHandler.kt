package ru.alexey.datacontainer.eventhandler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.alexey.datacontainer.EventsWorkerHost
import ru.alexey.datacontainer.eventbus.Event
import ru.alexey.datacontainer.eventbus.EventBus

interface EventsHandler {
    suspend fun handleEvent(event: Event)
}

interface EventsWorker {
    suspend fun postEvent(event: Event)
}


fun EventsWorkerHost.eventWorker(
    eventBusFactory: () -> EventBus = { EventBus.default },
    eventsHandler: EventsHandler,
    coroutineScope: CoroutineScope
): EventsWorker {
    return object: EventsWorker {
        private val eventBus: EventBus = eventBusFactory()
        override suspend fun postEvent(event: Event) {
            eventBus.emit(event)
        }
        init {
            coroutineScope.launch { eventBus.collect { eventsHandler.handleEvent(it) } }
        }
    }
}

inline fun <U, STATE> EventsWorkerHost.wrapContainer(
    eventBus: EventBus,
    coroutineScope: CoroutineScope,
    mutationSource: U,
    crossinline mutation: suspend U.(Event) -> STATE
): EventsWorker {
    return object : EventsWorker {
        override suspend fun postEvent(event: Event) {
            eventBus.emit(event)
        }
        init {
            coroutineScope.launch { eventBus.collect { with(it) { mutationSource.mutation(it) } } }
        }
    }
}

fun EventsWorkerHost.eventWorkerWithBus(
    eventBusFactory: () -> EventBus = { EventBus.default },
    coroutineScope: CoroutineScope,
    eventsHandler: EventsHandler,
): EventsWorker {
    return object: EventsWorker {
        private val eventBus: EventBus = eventBusFactory()
        override suspend fun postEvent(event: Event) {
            eventBus.emit(event)
        }
        init {
            coroutineScope.launch { eventBus.collect { eventsHandler.handleEvent(it) } }
        }
    }
}

fun EventsWorkerHost.eventWorkerWithBus(
    eventBus: EventBus = EventBus.default,
    coroutineScope: CoroutineScope,
    eventsHandler: EventsHandler,
): EventsWorker {
    return object: EventsWorker {
        override suspend fun postEvent(event: Event) {
            eventBus.emit(event)
        }
        init {
            coroutineScope.launch { eventBus.collect { eventsHandler.handleEvent(it) } }
        }
    }
}