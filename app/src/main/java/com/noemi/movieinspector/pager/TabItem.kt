package com.noemi.movieinspector.pager

import com.noemi.movieinspector.R

enum class TabItem(val tab: Int) {
    TOP_RATED(tab = R.string.label_top_rated),
    POPULAR(tab = R.string.label_popular),
    FAVORITE(tab = R.string.label_favorite);

    companion object {
        fun getMovieTabs() = listOf(TOP_RATED, POPULAR, FAVORITE)
    }
}