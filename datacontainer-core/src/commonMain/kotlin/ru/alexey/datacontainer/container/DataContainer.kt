package ru.alexey.datacontainer.container

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import ru.alexey.datacontainer.DataContainerHost
import ru.alexey.datacontainer.EventsWorkerHost
import ru.alexey.datacontainer.eventbus.Event
import ru.alexey.datacontainer.eventbus.EventBus
import ru.alexey.datacontainer.eventhandler.EventsWorker
import ru.alexey.datacontainer.eventhandler.wrapContainer
import ru.alexey.datacontainer.transform.Transform


class DataContainer<STATE : Any, U>(
    innerState: U,
    state: StateFlow<STATE>,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
    actionHandler: suspend U.(Event) -> Unit,
    eventBus: EventBus,
): StateFlow<STATE> by state, EventsWorkerHost {

    override val eventsWorker: EventsWorker
        = wrapContainer(
            eventBus,
            coroutineScope,
            innerState,
            actionHandler
        )
}

typealias FlowDataContainer<STATE> = DataContainer<STATE, MutableStateFlow<STATE>>

fun <STATE : Any> DataContainerHost<STATE>.container(
    initial: STATE,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
    transforms: List<Transform<Any, STATE>> = emptyList(),
    eventBus: EventBus,
    actionHandler: suspend MutableStateFlow<STATE>.(Event) -> Unit,
): DataContainer<STATE, MutableStateFlow<STATE>> {
    val innerState = MutableStateFlow(initial)
    val state: StateFlow<STATE> = transforms.fold(innerState as Flow<STATE>) { acc, (flow, transform) ->
        flow.combine(acc, transform)
    }.stateIn(
        scope = coroutineScope,
        initialValue = initial,
        started = SharingStarted.Lazily
    )
    return DataContainer<STATE, MutableStateFlow<STATE>>(
        innerState = innerState,
        state = state,
        coroutineScope = coroutineScope,
        actionHandler = actionHandler,
        eventBus = eventBus
    )
}