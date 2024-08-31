package com.noemi.movieinspector.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.paging.PagingSource
import androidx.room.Room
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.noemi.movieinspector.connection.ConnectionService
import com.noemi.movieinspector.connection.ConnectionServiceImpl
import com.noemi.movieinspector.model.Movie
import com.noemi.movieinspector.network.MovieService
import com.noemi.movieinspector.paging.MoviePaging
import com.noemi.movieinspector.paging.popular.PopularPaging
import com.noemi.movieinspector.paging.popular.PopularPagingConfig
import com.noemi.movieinspector.paging.toprated.TopRatedPaging
import com.noemi.movieinspector.paging.toprated.TopRatedPagingConfig
import com.noemi.movieinspector.utils.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import com.noemi.movieinspector.repository.MovieRepository
import com.noemi.movieinspector.repository.MovieRepositoryImpl
import com.noemi.movieinspector.room.MovieDAO
import com.noemi.movieinspector.room.MovieDataBase
import com.noemi.movieinspector.utils.MOVIE_DB
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesJson(): Json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(interceptor).build()

    @Provides
    @Singleton
    @ExperimentalSerializationApi
    fun providesMoviesService(json: Json, client: OkHttpClient): MovieService =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(MovieService::class.java)

    @Provides
    @Singleton
    fun providesDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun providesMovieDataBase(@ApplicationContext context: Context): MovieDataBase =
        Room.databaseBuilder(context, MovieDataBase::class.java, MOVIE_DB).build()

    @Provides
    @Singleton
    fun providesMovieDao(dataBase: MovieDataBase): MovieDAO = dataBase.getMovieDao()

    @Provides
    @Singleton
    fun providesMovieRepository(
        service: MovieService,
        dispatcher: CoroutineDispatcher,
        movieDAO: MovieDAO
    ): MovieRepository = MovieRepositoryImpl(service, dispatcher, movieDAO)

    @Provides
    @Singleton
    fun providesConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @Singleton
    fun providesScope(): CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Provides
    @Singleton
    fun providesConnectivityService(manager: ConnectivityManager, scope: CoroutineScope): ConnectionService =
        ConnectionServiceImpl(manager, scope)

    @Provides
    @Singleton
    fun providesTopRatedPaging(service: MovieService): PagingSource<Int, Movie> = TopRatedPaging(service)

    @Provides
    @Singleton
    fun providesTopRatedPagingConfig(paging: TopRatedPaging, dispatcher: CoroutineDispatcher): MoviePaging =
        TopRatedPagingConfig(paging, dispatcher)

    @Provides
    @Singleton
    fun providesPopularPaging(service: MovieService): PagingSource<Int, Movie> = PopularPaging(service)

    @Provides
    @Singleton
    fun providesPopularConfig(paging: PopularPaging, dispatcher: CoroutineDispatcher): MoviePaging =
        PopularPagingConfig(paging, dispatcher)
}