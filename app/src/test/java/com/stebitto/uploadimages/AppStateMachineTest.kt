package com.stebitto.uploadimages

import android.net.Uri
import app.cash.turbine.test
import com.stebitto.uploadimages.actions.PickedImages
import com.stebitto.uploadimages.actions.RetryLoadingCountries
import com.stebitto.uploadimages.datamodels.domain.AppImage
import com.stebitto.uploadimages.statemachines.AppStateMachine
import com.stebitto.uploadimages.states.CountryState
import com.stebitto.uploadimages.states.UploadImagesState
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AppStateMachineTest {

    private lateinit var fakeCountriesRepository: FakeCountriesRepository
    private lateinit var fakeUploadImagesRepository: FakeUploadImagesRepository

    @Before
    fun setupMockAndEmptyTestDoubles() {
        mockkStatic(Uri::class)
        val uriMock = mockk<Uri>()
        every { Uri.parse("") } returns uriMock

        fakeCountriesRepository = FakeCountriesRepository()
        fakeUploadImagesRepository = FakeUploadImagesRepository()
    }

    @After
    fun removeMock() {
        unmockkAll()
    }

    @Test
    fun `state machine starts with Loading and transition to CountryList on successful country request`() = runTest {
        val appStateMachine = AppStateMachine(fakeCountriesRepository, fakeUploadImagesRepository)
        appStateMachine.state.test {
            // starting from Loading state
            assertEquals(CountryState.Loading, awaitItem())
            // state machine automatically transition to CountryList state on success
            assertEquals(CountryState.CountryList(emptyList()), awaitItem())
        }
    }

    @Test
    fun `state machine starts with Loading and transition to Error on failed country request`() = runTest {
        val fakeCountriesRepository = FakeCountriesRepository(exception = Exception())

        val appStateMachine = AppStateMachine(fakeCountriesRepository, fakeUploadImagesRepository)
        appStateMachine.state.test {
            // starting from Loading state
            assertEquals(CountryState.Loading, awaitItem())
            // state machine automatically transition to Error state on failure
            assertEquals(CountryState.Error(), awaitItem())
        }
    }

    @Test
    fun `move to Error from Loading on RetryLoading action`() = runTest {
        val appStateMachine = AppStateMachine(
            fakeCountriesRepository,
            fakeUploadImagesRepository,
            CountryState.Error()
        )

        appStateMachine.state.test {
            // when state machine is in Error state
            assertEquals(CountryState.Error(), awaitItem())
            // and retry action is dispatched
            appStateMachine.dispatch(RetryLoadingCountries)
            // state machine transition to Loading state
            assertEquals(CountryState.Loading, awaitItem())
            // ignore following state
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `add new images to upload with PickedImages action without changing state (empty state)`() = runTest {
        val pickedImages = List(3) { AppImage(contentUri = Uri.parse("")) }
        val pickedImageState = UploadImagesState.PickImages(imagesToUpload = pickedImages)

        val appStateMachine = AppStateMachine(
            fakeCountriesRepository,
            fakeUploadImagesRepository,
            UploadImagesState.PickImages()
        )

        appStateMachine.state.test {
            assertEquals(UploadImagesState.PickImages(), awaitItem())
            appStateMachine.dispatch(PickedImages(pickedImages))
            assertEquals(pickedImageState, awaitItem())
        }
    }

    @Test
    fun `add new images to upload with PickedImages action without changing state (populated state)`() = runTest {
        val currentList = List(5) { AppImage(contentUri = Uri.parse("")) }
        val initialState = UploadImagesState.PickImages(imagesToUpload = currentList)
        val pickedImages = List(3) { AppImage(contentUri = Uri.parse("")) }
        val pickedImageState = UploadImagesState.PickImages(imagesToUpload = currentList + pickedImages)

        val appStateMachine = AppStateMachine(
            fakeCountriesRepository,
            fakeUploadImagesRepository,
            initialState
        )

        appStateMachine.state.test {
            assertEquals(initialState, awaitItem())
            appStateMachine.dispatch(PickedImages(pickedImages))
            assertEquals(pickedImageState, awaitItem())
        }
    }
}