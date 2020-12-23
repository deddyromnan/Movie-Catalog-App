package com.romnan.moviecatalog.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.romnan.moviecatalog.data.model.TvSeriesDetail
import com.romnan.moviecatalog.data.model.movie.MovieDetail
import com.romnan.moviecatalog.data.model.movie.PopularMovie
import com.romnan.moviecatalog.data.model.tvseries.PopularTvSeries
import com.romnan.moviecatalog.data.source.local.LocalDataSource
import com.romnan.moviecatalog.data.source.remote.RemoteDataSource
import com.romnan.moviecatalog.utils.AppExecutors


class FakeMovieCatalogRepository constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
) : MovieCatalogDataSource {

    companion object {
        @Volatile
        private var instance: FakeMovieCatalogRepository? = null

        fun getInstance(
            remoteDataSource: RemoteDataSource,
            localDataSource: LocalDataSource,
            appExecutors: AppExecutors
        ): FakeMovieCatalogRepository =
            instance ?: synchronized(this) {
                instance ?: FakeMovieCatalogRepository(
                    remoteDataSource,
                    localDataSource,
                    appExecutors
                )
            }
    }

    override fun getPopularMovies(): DataSource.Factory<Int, PopularMovie> =
        remoteDataSource.getPopularMovies()

    override fun getPopularTvSeries(): DataSource.Factory<Int, PopularTvSeries> =
        remoteDataSource.getPopularTvSeries()


    override fun getMovieDetail(movieId: Int): LiveData<MovieDetail> {
        val result = MutableLiveData<MovieDetail>()

        remoteDataSource.getMovieDetail(movieId, object : RemoteDataSource.LoadMovieDetailCallback {
            override fun onMovieDetailReceived(movieDetailResponse: MovieDetail) {
                result.postValue(movieDetailResponse)
            }
        })

        return result
    }

    override fun getTvSeriesDetail(tvSeriesId: Int): LiveData<TvSeriesDetail> {
        val result = MutableLiveData<TvSeriesDetail>()

        remoteDataSource.getTvSeriesDetail(
            tvSeriesId,
            object : RemoteDataSource.LoadTvSeriesDetailCallback {
                override fun onTvSeriesDetailReceived(tvSeriesDetailResponse: TvSeriesDetail) {
                    result.postValue(tvSeriesDetailResponse)
                }
            })

        return result
    }

    override fun insertFavoriteMovie(movie: MovieDetail) =
        appExecutors.diskIO().execute {
            localDataSource.insertFavoriteMovie(movie)
        }

    override fun insertFavoriteTvSeries(tvSeries: TvSeriesDetail) =
        appExecutors.diskIO().execute {
            localDataSource.insertFavoriteTvSeries(tvSeries)
        }

    override fun deleteFavoriteMovie(movie: MovieDetail) =
        appExecutors.diskIO().execute {
            localDataSource.deleteFavoriteMovie(movie)
        }

    override fun deleteFavoriteTvSeries(tvSeries: TvSeriesDetail) =
        appExecutors.diskIO().execute {
            localDataSource.deleteFavoriteTvSeries(tvSeries)
        }

    override fun getFavoriteMovies(): DataSource.Factory<Int, MovieDetail> {
        return localDataSource.getFavoriteMovies()
    }

    override fun getFavoriteTvSeries(): DataSource.Factory<Int, TvSeriesDetail> {
        return localDataSource.getFavoriteTvSeries()
    }

    override fun isFavoriteMovie(movieId: Int): LiveData<Boolean> =
        localDataSource.isFavoriteMovie(movieId)


    override fun isFavoriteTvSeries(tvSeriesId: Int): LiveData<Boolean> =
        localDataSource.isFavoriteTvSeries(tvSeriesId)
}