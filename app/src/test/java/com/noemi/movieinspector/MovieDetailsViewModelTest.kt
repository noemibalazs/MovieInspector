package com.noemi.movieinspector

import android.app.Application
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.noemi.movieinspector.model.Movie
import com.noemi.movieinspector.model.Review
import com.noemi.movieinspector.model.Trailer
import com.noemi.movieinspector.repository.MovieRepository
import com.noemi.movieinspector.room.MovieEntity
import com.noemi.movieinspector.screens.details.MovieDetailsViewModel
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
class MovieDetailsViewModelTest {

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private val repository: MovieRepository = mockk()
    private val application: Application = mockk()

    private lateinit var viewModel: MovieDetailsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = MovieDetailsViewModel(application = application, repository = repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test get all movie details should be successful`() = runBlocking {
        val movie = mockk<Movie>()
        val entity = mockk<MovieEntity>()

        val title = "Miss Sloane"
        val description = "Great movie"
        val id = 12
        val rating = 9.99
        val path = "poster path"

        val job = launch {
            assertThat(viewModel.loadingState.value).isTrue()
            assertThat(viewModel.outcomeState.value).isEqualTo(Movie())
            assertThat(viewModel.errorState.value).isEmpty()
            assertThat(viewModel.trailersState.value).isEqualTo(emptyList<Trailer>())
            assertThat(viewModel.reviewsState.value).isEqualTo(emptyList<Review>())

            coEvery { entity.title } returns title
            coEvery { entity.description } returns description
            coEvery { entity.id } returns id
            coEvery { entity.rating } returns rating
            coEvery { entity.posterPath } returns path

            mockkStatic(MovieEntity::toMovie)
            coEvery { entity.toMovie() } returns movie

            coEvery { repository.getMovie(id) } returns entity

            viewModel.outcomeState.test {
                val item = awaitItem()
                assertThat(item).isEqualTo(movie)

                viewModel.trailersState.test {
                    val trailers = awaitItem()

                    repository.loadTrailers(id).test {
                        val result = awaitItem()
                        assertThat(result).isEqualTo(trailers)
                    }
                }

                viewModel.reviewsState.test {
                    val reviews = awaitItem()

                    repository.loadReviews(id).test {
                        val result = awaitItem()
                        assertThat(result).isEqualTo(reviews)
                    }
                }

                assertThat(viewModel.loadingState.value).isFalse()

                cancelAndConsumeRemainingEvents()
            }

            coVerify { repository.getMovie(id) }
        }

        viewModel.getMovieDetails(id)

        job.cancelAndJoin()
    }

    @Test
    fun `test get movie details should be successful trailers reviews throws exception`() = runBlocking {
        val movie = mockk<Movie>()
        val entity = mockk<MovieEntity>()

        val title = "Miss Sloane"
        val description = "Great movie"
        val id = 12
        val rating = 9.99
        val path = "poster path"

        val job = launch {
            assertThat(viewModel.loadingState.value).isTrue()
            assertThat(viewModel.outcomeState.value).isEqualTo(Movie())
            assertThat(viewModel.errorState.value).isEmpty()
            assertThat(viewModel.trailersState.value).isEqualTo(emptyList<Trailer>())
            assertThat(viewModel.reviewsState.value).isEqualTo(emptyList<Review>())

            coEvery { entity.title } returns title
            coEvery { entity.description } returns description
            coEvery { entity.id } returns id
            coEvery { entity.rating } returns rating
            coEvery { entity.posterPath } returns path

            mockkStatic(MovieEntity::toMovie)
            coEvery { entity.toMovie() } returns movie

            coEvery { repository.getMovie(id) } returns entity

            viewModel.outcomeState.test {
                val item = awaitItem()
                assertThat(item).isEqualTo(movie)

                viewModel.trailersState.test {
                    val trailers = awaitItem()

                    repository.loadTrailers(id).test {
                        val result = awaitError()

                        assertThat(viewModel.errorState.value).isEqualTo(result.message)
                        assertThat(trailers.isEmpty()).isTrue()
                    }
                }

                viewModel.reviewsState.test {
                    val reviews = awaitItem()

                    repository.loadReviews(id).test {
                        val result = awaitError()

                        assertThat(viewModel.errorState.value).isEqualTo(result.message)
                        assertThat(reviews.isEmpty()).isTrue()
                    }
                }

                assertThat(viewModel.loadingState.value).isFalse()

                cancelAndConsumeRemainingEvents()
            }

            coVerify { repository.getMovie(id) }
        }

        viewModel.getMovieDetails(id)

        job.cancelAndJoin()
    }

    @Test
    fun `test get trailers reviews should be successful movie details throws exception`() = runBlocking {
        val error = mockk<Throwable>()
        val id = 12

        val job = launch {
            assertThat(viewModel.loadingState.value).isTrue()
            assertThat(viewModel.outcomeState.value).isEqualTo(Movie())
            assertThat(viewModel.errorState.value).isEmpty()
            assertThat(viewModel.trailersState.value).isEqualTo(emptyList<Trailer>())
            assertThat(viewModel.reviewsState.value).isEqualTo(emptyList<Review>())

            coEvery { repository.getMovie(id) } throws error

            viewModel.outcomeState.test {
                val errorResponse = awaitError()
                assertThat(errorResponse).isEqualTo(error)
                assertThat(viewModel.errorState.value).isEqualTo(errorResponse.message)

                viewModel.trailersState.test {
                    val trailers = awaitItem()

                    repository.loadTrailers(id).test {
                        val result = awaitItem()
                        assertThat(result).isEqualTo(trailers)
                    }
                }

                viewModel.reviewsState.test {
                    val reviews = awaitItem()

                    repository.loadReviews(id).test {
                        val result = awaitItem()
                        assertThat(result).isEqualTo(reviews)
                    }
                }

                assertThat(viewModel.loadingState.value).isFalse()

                cancelAndConsumeRemainingEvents()
            }

            coVerify { repository.getMovie(id) }
        }

        viewModel.getMovieDetails(id)

        job.cancelAndJoin()
    }

}