package com.romnan.moviecatalog.core.presentation

data class TvSeriesPresentation(
    val id: Int,
    val posterPath: String,
    val name: String,
    val firstAirDate: String,
    val overview: String,
    val backdropPath: String,
    val voteAverage: Double,
    val tagline: String,
    val numberOfSeasons: Int,
    val status: String,
    val type: String,
    val isFavorite: Boolean
)