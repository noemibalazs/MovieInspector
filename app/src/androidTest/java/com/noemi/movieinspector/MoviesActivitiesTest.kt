package com.noemi.movieinspector

import androidx.activity.compose.setContent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.noemi.movieinspector.screens.main.MoviesActivity
import com.noemi.movieinspector.screens.main.MoviesApp
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class MoviesActivitiesTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MoviesActivity>()

    @Before
    fun setupMastodonApp() {
        composeRule.activity.setContent {
            MoviesApp()
        }
    }

    @Test
    fun testTabRowPagerDisplayed() {
        composeRule.onNodeWithTag(composeRule.activity.getString(R.string.label_tab_row_tag)).assertIsDisplayed()
        val tab = composeRule.onNodeWithTag(composeRule.activity.getString(R.string.label_pager_tag))

        tab.onChildren()[0]
            .assertIsDisplayed()
            .performClick()

        composeRule.onNodeWithStringId(R.string.label_top_rated).assertIsDisplayed()
        composeRule.onNodeWithStringId(R.string.label_popular).assertIsDisplayed()
        composeRule.onNodeWithStringId(R.string.label_favorite).assertIsDisplayed()
    }


    @Test
    fun testLazyColumnDisplayed() {
        composeRule.waitUntilAtLeastOneExists(
            matcher = hasTestTag(composeRule.activity.getString(R.string.label_movie_item_tag)), 2100L
        )

        composeRule.onNodeWithTag(composeRule.activity.getString(R.string.label_lazy_column_tag))
            .onChildren()[0]
            .assertIsDisplayed()
            .performClick()
    }
}
