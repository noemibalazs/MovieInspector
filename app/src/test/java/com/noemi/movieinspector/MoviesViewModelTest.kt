package com.noemi.movieinspector

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.noemi.movieinspector.screens.main.MoviesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest {

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: MoviesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = MoviesViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test load top rated movies should be connected`() = runBlocking {

        val isActive = true

        val job = launch {
            assertThat(viewModel.networkState.value).isFalse()

            viewModel.networkState.test {
                val result = awaitItem()
                assertThat(result).isEqualTo(isActive)

                cancelAndConsumeRemainingEvents()
            }
        }

        viewModel.onNetworkStateChanged(isActive)

        job.cancelAndJoin()
    }

    @Test
    fun `test load top rated movies should be not connected`() = runBlocking {

        val isActive = false

        val job = launch {
            assertThat(viewModel.networkState.value).isFalse()

            viewModel.networkState.test {
                val result = awaitItem()
                assertThat(result).isEqualTo(isActive)

                cancelAndConsumeRemainingEvents()
            }
        }

        viewModel.onNetworkStateChanged(isActive)

        job.cancelAndJoin()
    }
}