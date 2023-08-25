package com.stebitto.uploadimages

import app.cash.turbine.test
import com.stebitto.uploadimages.statemachines.AppStateMachine
import com.stebitto.uploadimages.states.CountryState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AppStateMachineTest {

    @Test
    fun `state machine starts with Loading and transition to CountryList on successful country request`() = runTest {
        val fakeCountriesRepository = FakeCountriesRepository()
        val fakeUploadImagesRepository = FakeUploadImagesRepository()

        val appStateMachine = AppStateMachine(fakeCountriesRepository, fakeUploadImagesRepository)
        appStateMachine.state.test {
            assertEquals(CountryState.Loading, awaitItem())
            assertEquals(CountryState.CountryList(emptyList()), awaitItem())
        }
    }

    @Test
    fun `state machine starts with Loading and transition to Error on failed country request`() = runTest {
        val fakeCountriesRepository = FakeCountriesRepository(exception = Exception())
        val fakeUploadImagesRepository = FakeUploadImagesRepository()

        val appStateMachine = AppStateMachine(fakeCountriesRepository, fakeUploadImagesRepository)
        appStateMachine.state.test {
            assertEquals(CountryState.Loading, awaitItem())
            assertEquals(CountryState.Error(null), awaitItem())
        }
    }
}