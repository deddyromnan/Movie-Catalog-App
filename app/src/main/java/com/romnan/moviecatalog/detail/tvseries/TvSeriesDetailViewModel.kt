package com.romnan.moviecatalog.detail.tvseries

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romnan.moviecatalog.core.data.Resource
import com.romnan.moviecatalog.core.domain.usecase.MovieCatalogUseCase
import com.romnan.moviecatalog.core.presentation.tvseries.TvSeriesDetail
import com.romnan.moviecatalog.core.util.TvSeriesMapper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TvSeriesDetailViewModel(private val useCase: MovieCatalogUseCase) : ViewModel() {
    private val _tvSeries = MutableLiveData<TvSeriesDetail>()
    val tvSeries: LiveData<TvSeriesDetail> = _tvSeries

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getTvSeriesDetail(tvSeriesId: Int) {
        viewModelScope.launch {
            useCase.getTvSeriesDetail(tvSeriesId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> _isLoading.postValue(true)
                    is Resource.Success -> {
                        val data = resource.data?.let { TvSeriesMapper.domainToDetail(it) }
                        _tvSeries.postValue(data)
                        _isLoading.postValue(false)
                    }
                    is Resource.Error -> {
                        _errorMessage.postValue(resource.message)
                        _isLoading.postValue(false)
                    }
                }
            }
        }
    }

    fun setFavoriteTvSeries(tvSeriesPresented: TvSeriesDetail, newState: Boolean) {
        val tvSeries = TvSeriesMapper.detailToDomain(tvSeriesPresented)
        useCase.setFavoriteTvSeries(tvSeries, newState)
    }
}