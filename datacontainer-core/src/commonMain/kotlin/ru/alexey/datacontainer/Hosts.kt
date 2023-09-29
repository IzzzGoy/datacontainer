package ru.alexey.datacontainer

import kotlinx.coroutines.channels.SendChannel
import ru.alexey.datacontainer.container.DataContainer
import ru.alexey.datacontainer.eventhandler.EventsWorker

interface DataContainerHost<STATE : Any> {
    val container: DataContainer<STATE, *>
}

interface EventsWorkerHost {
    val eventsWorker: EventsWorker
}


