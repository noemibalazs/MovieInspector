package com.noemi.movieinspector.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noemi.movieinspector.pager.TabItem
import com.noemi.movieinspector.screens.favorite.FavoriteScreen
import com.noemi.movieinspector.screens.popular.PopularScreen
import com.noemi.movieinspector.screens.toprated.TopRatedScreen
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noemi.movieinspector.R
import com.noemi.movieinspector.utils.NoNetworkConnection
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoviesApp() {

    val viewModel = hiltViewModel<MoviesViewModel>()
    val hasNetworkConnection by viewModel.networkState.collectAsStateWithLifecycle()

    val tabs = TabItem.getMovieTabs().map { it.tab }
    val pagerState = rememberPagerState(pageCount = { tabs.size }, initialPage = 0)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MovieTabLayout(tabs = tabs, pagerState = pagerState)

        when (hasNetworkConnection) {
            true -> MovieTabContent(pagerState = pagerState)
            else -> NoNetworkConnection()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun MovieTabLayout(tabs: List<Int>, pagerState: PagerState, modifier: Modifier = Modifier) {

    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    PrimaryTabRow(
        selectedTabIndex = tabIndex,
        modifier = modifier
            .fillMaxWidth()
            .testTag(stringResource(id = R.string.label_tab_row_tag)),
        containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
        indicator = {
            MovieIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = modifier.tabIndicatorOffset(tabIndex)
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = tabIndex == index,
                unselectedContentColor = Color.White,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = {
                    Text(
                        text = stringResource(id = title),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    }
}

@Composable
private fun MovieIndicator(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(6.dp)
            .fillMaxSize()
            .border(
                border = BorderStroke(2.dp, color),
                shape = RoundedCornerShape(6.dp)
            )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MovieTabContent(pagerState: PagerState) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .testTag(stringResource(id = R.string.label_pager_tag))
    ) { index ->
        when (index) {
            0 -> TopRatedScreen()
            1 -> PopularScreen()
            else -> FavoriteScreen()
        }
    }
}
