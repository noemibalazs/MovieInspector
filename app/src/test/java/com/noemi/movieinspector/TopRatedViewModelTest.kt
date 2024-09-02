package com.noemi.movieinspector

import android.app.Application
import androidx.paging.PagingData
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.noemi.movieinspector.model.Movie
import com.noemi.movieinspector.paging.toprated.TopRatedPagingConfig
import com.noemi.movieinspector.repository.MovieRepository
import com.noemi.movieinspector.screens.toprated.TopRatedViewModel
import io.kotest.matchers.shouldBe
import io.mockk.mockk
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
class TopRatedViewModelTest {
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private val topRatedPagingConfig: TopRatedPagingConfig = mockk()
    private val repository: MovieRepository = mockk()
    private val application: Application = mockk()
    private lateinit var viewModel: TopRatedViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = TopRatedViewModel(
            application = application,
            repository = repository,
            topRatedPagingConfig = topRatedPagingConfig,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test load top rated movies should be successful`() = runBlocking {

        val job = launch {
            assertThat(viewModel.loadingState.value).isTrue()
            assertThat(viewModel.outcomeState.value == PagingData.empty<Movie>()).isTrue()

            viewModel.outcomeState.test {
                val result = awaitItem()

                topRatedPagingConfig.loadMovies().test {
                    val pagingData = awaitItem()
                    assertThat(result).isEqualTo(pagingData)
                    assertThat(viewModel.loadingState.value).isFalse()
                }

                cancelAndConsumeRemainingEvents()
            }
        }

        viewModel.loadTopRatedMovies()

        job.cancelAndJoin()
    }

    @Test
    fun `test load top rated movies should throw an error`() = runBlocking {

        val job = launch {

            assertThat(viewModel.loadingState.value).isTrue()
            assertThat(viewModel.errorState.value).isEmpty()
            assertThat(viewModel.outcomeState.value == PagingData.empty<Movie>()).isTrue()

            viewModel.outcomeState.test {
                val result = awaitItem()

                topRatedPagingConfig.loadMovies().test {
                    val pagingError = awaitError()

                    assertThat(viewModel.errorState.value).isEqualTo(pagingError.message)
                    assertThat(viewModel.loadingState.value).isFalse()
                    assertThat(result).shouldBe(PagingData.empty<Movie>())
                }

                cancelAndConsumeRemainingEvents()
            }
        }

        viewModel.loadTopRatedMovies()

        job.cancelAndJoin()
    }
}