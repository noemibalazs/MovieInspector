package com.noemi.movieinspector

import android.app.Application
import android.content.Context
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.noemi.movieinspector.model.Movie
import com.noemi.movieinspector.repository.MovieRepository
import com.noemi.movieinspector.room.MovieEntity
import com.noemi.movieinspector.screens.favorite.FavoriteViewModel
import com.noemi.movieinspector.utils.toEntity
import com.noemi.movieinspector.utils.toMovie
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
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
class FavoriteViewModelTest {

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private val repository: MovieRepository = mockk()
    private val application: Application = mockk()

    private lateinit var viewModel: FavoriteViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = FavoriteViewModel(
            application = application,
            repository = repository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test load favorite movies should be successful`() = runBlocking {

        val job = launch {
            assertThat(viewModel.loadingState.value).isTrue()
            assertThat(viewModel.outcomeState.value.isEmpty()).isTrue()

            viewModel.outcomeState.test {
                val result = awaitItem()

                repository.observeMovies().test {
                    val event = awaitItem()
                    assertThat(result).isEqualTo(event.map { it.toMovie() })
                    assertThat(viewModel.loadingState.value).isFalse()
                }

                cancelAndConsumeRemainingEvents()
            }
        }

        viewModel.loadFavoriteMovies()

        job.cancelAndJoin()
    }

    @Test
    fun `test load favorite movies should throw an error`() = runBlocking {

        val job = launch {
            assertThat(viewModel.loadingState.value).isTrue()
            assertThat(viewModel.errorState.value.isEmpty()).isTrue()

            viewModel.outcomeState.test {
                val movies = awaitItem()

                repository.observeMovies().test {
                    val eventError = awaitError()

                    assertThat(viewModel.errorState.value).isEqualTo(eventError.message)
                    assertThat(viewModel.loadingState.value).isFalse()
                    assertThat(movies.isEmpty()).isTrue()
                }

                cancelAndConsumeRemainingEvents()
            }
        }

        viewModel.loadFavoriteMovies()

        job.cancelAndJoin()
    }

    @Test
    fun `test save movie`() = runBlocking {
        val movie = mockk<Movie>()
        val context = mockk<Context>()
        val entity = mockk<MovieEntity>()

        val title = "Miss Sloane"
        val description = "Great movie"
        val id = 12
        val rating = 9.99
        val path = "poster path"

        val job = launch {

            coEvery { movie.title } returns title
            coEvery { movie.description } returns description
            coEvery { movie.id } returns id
            coEvery { movie.rating } returns rating
            coEvery { movie.posterPath } returns path

            mockkStatic(Movie::toEntity)
            coEvery { movie.toEntity(context) } returns entity

            coEvery { repository.insertMovie(entity) }

            repository.observeMovies().test {
                val result = awaitItem()
                assertThat(result.size == 1).isTrue()

                cancelAndConsumeRemainingEvents()
            }

            coVerify { repository.insertMovie(entity) }
        }

        viewModel.saveMovie(movie)

        job.cancelAndJoin()
    }
}