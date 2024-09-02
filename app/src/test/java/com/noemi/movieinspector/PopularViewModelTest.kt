package com.noemi.movieinspector

import android.app.Application
import androidx.paging.PagingData
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.noemi.movieinspector.model.Movie
import com.noemi.movieinspector.paging.popular.PopularPagingConfig
import com.noemi.movieinspector.repository.MovieRepository
import com.noemi.movieinspector.screens.popular.PopularViewModel
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
class PopularViewModelTest {
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private val popularPagingConfig: PopularPagingConfig = mockk()
    private val repository: MovieRepository = mockk()
    private val application: Application = mockk()

    private lateinit var viewModel: PopularViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = PopularViewModel(
            application = application,
            repository = repository,
            popularPagingConfig = popularPagingConfig
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test load popular movies should be successful`() = runBlocking {

        val job = launch {
            assertThat(viewModel.loadingState.value).isTrue()
            assertThat(viewModel.outcomeState.value == PagingData.empty<Movie>()).isTrue()

            viewModel.outcomeState.test {
                val result = awaitItem()

                popularPagingConfig.loadMovies().test {

                    val pagingData = awaitItem()
                    assertThat(result).isEqualTo(pagingData)
                    assertThat(viewModel.loadingState.value).isFalse()
                }

                cancelAndConsumeRemainingEvents()
            }
        }

        viewModel.loadPopularMovies()

        job.cancelAndJoin()
    }

    @Test
    fun `test load popular movies should throw an error`() = runBlocking {

        val job = launch {
            assertThat(viewModel.loadingState.value).isTrue()
            assertThat(viewModel.errorState.value).isEmpty()
            assertThat(viewModel.outcomeState.value == PagingData.empty<Movie>()).isTrue()

            viewModel.outcomeState.test {
                val movies = awaitItem()

                popularPagingConfig.loadMovies().test {
                    val pagingError = awaitError()

                    assertThat(viewModel.errorState.value).isEqualTo(pagingError.message)
                    assertThat(viewModel.loadingState.value).isFalse()
                    assertThat(movies.shouldBe(PagingData.empty()))
                }

                cancelAndConsumeRemainingEvents()
            }
        }

        viewModel.loadPopularMovies()

        job.cancelAndJoin()
    }
}