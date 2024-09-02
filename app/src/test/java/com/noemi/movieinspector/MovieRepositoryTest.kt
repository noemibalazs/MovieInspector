package com.noemi.movieinspector

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.noemi.movieinspector.network.MovieService
import com.noemi.movieinspector.repository.MovieRepository
import com.noemi.movieinspector.repository.MovieRepositoryImpl
import com.noemi.movieinspector.room.MovieDAO
import com.noemi.movieinspector.room.MovieEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieRepositoryTest {

    private val movieDAO: MovieDAO = mockk()
    private val service: MovieService = mockk()
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: MovieRepository

    private val movie = mockk<MovieEntity>()
    private val id = 12

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = MovieRepositoryImpl(movieDAO = movieDAO, dispatcher = dispatcher, service = service)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `observe movies returns a flow list of movie entity`() = runTest {

        coEvery { movieDAO.observeMovies() } returns flowOf(listOf(movie))

        repository.observeMovies().test {
            assertThat(awaitItem()).isEqualTo(listOf(movie))
            cancelAndConsumeRemainingEvents()
        }

        coVerify { movieDAO.observeMovies() }
    }

    @Test
    fun `insert movie should be successful`() = runTest {

        coEvery { movieDAO.insertMovie(movie) } just runs

        repository.insertMovie(movie)

        coVerify { movieDAO.insertMovie(movie) }
    }

    @Test
    fun `get movie by id should be successful`() = runTest {

        coEvery { movieDAO.getMovie(id) } returns movie

        repository.getMovie(id)

        coVerify { movieDAO.getMovie(id) }
    }
}