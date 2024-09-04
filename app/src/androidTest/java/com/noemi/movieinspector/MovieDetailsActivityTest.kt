package com.noemi.movieinspector

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.noemi.movieinspector.screens.details.DetailsScreen
import com.noemi.movieinspector.screens.details.MovieDetailsActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MovieDetailsActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MovieDetailsActivity>()

    @Before
    fun setupNewsApp() {
        composeRule.activity.setContent {
            DetailsScreen(movieId = 278)
        }
    }

    @Test
    fun testContainerDisplayed() {

        composeRule.waitUntil {
            composeRule.onNodeWithTag(composeRule.activity.getString(R.string.label_movie_container_tag)).isDisplayed()
        }

        composeRule.onNodeWithTag(composeRule.activity.getString(R.string.label_movie_content_tag)).assertIsDisplayed()
        composeRule.onNodeWithTag(composeRule.activity.getString(R.string.label_movie_title_tag)).assertIsDisplayed()
        composeRule.onNodeWithTag(composeRule.activity.getString(R.string.label_summary_tag)).assertIsDisplayed()
        composeRule.onNodeWithTag(composeRule.activity.getString(R.string.label_icon_down_tag)).assertIsDisplayed()
        composeRule.onNodeWithStringId(R.string.label_trailers).assertIsDisplayed()
        composeRule.onNodeWithTag(composeRule.activity.getString(R.string.label_movie_screenshot_tag)).isDisplayed()
    }
}