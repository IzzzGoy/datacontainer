package ru.alexey.datacontainer.cache

import androidx.datastore.core.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ru.alexey.datacontainer.DataContainerHost
import ru.alexey.datacontainer.container.DataContainer
import ru.alexey.datacontainer.eventbus.Event
import ru.alexey.datacontainer.eventbus.EventBus
import ru.alexey.datacontainer.transform.Transform


inline fun <reified STATE: Any, reified U: DataStore<STATE>> DataContainerHost<STATE>.wrapDatastore(
    initial: STATE,
    datastore: U,
    coroutineScope: CoroutineScope,
    transforms: List<Transform<Any, STATE>> = emptyList(),
    eventBus: EventBus,
    noinline actionHandler: suspend U.(Event) -> Unit,
): DataContainer<STATE, U> {
    val state: StateFlow<STATE> = transforms.fold(datastore.data) { acc, (flow, transform) ->
        flow.combine(acc, transform)
    }.stateIn(
        scope = coroutineScope,
        initialValue = initial,
        started = SharingStarted.Lazily
    )

    return DataContainer<STATE, U>(
        innerState = datastore,
        state = state,
        coroutineScope = coroutineScope,
        actionHandler = actionHandler,
        eventBus = eventBus,
    )

}

typealias DatastoreDataContainer<STATE> = DataContainer<STATE, DataStore<STATE>>