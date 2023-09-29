package ru.alexey.datacontainer

import ru.alexey.datacontainer.container.DataContainer
import ru.alexey.datacontainer.container.container
import kotlin.test.Test
import kotlin.test.assertEquals


class TestDataContainer {

    @Test
    fun creationTest() {

        val initial = 1

        val host: DataContainerHost<Int, Unit> = object : DataContainerHost<Int, Unit> {
            override val container: DataContainer<Int, Unit> = container(initial) {

            }
        }
        assertEquals(host.container.value, initial)

    }
}