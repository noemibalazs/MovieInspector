package com.noemi.movieinspector.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import com.noemi.movieinspector.R


val philosopherFamily = FontFamily(
    Font(R.font.philosopher_regular, FontWeight.Normal),
    Font(R.font.philosopher_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.philosopher_bold, FontWeight.Bold)
)

val Typography = Typography(

    bodyLarge = TextStyle(
        fontFamily = philosopherFamily,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    ),

    bodyMedium = TextStyle(
        fontFamily = philosopherFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        textDirection = TextDirection.Content
    ),

    bodySmall = TextStyle(
        fontFamily = philosopherFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),

    titleMedium = TextStyle(
        fontFamily = philosopherFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        fontStyle = FontStyle.Italic
    ),

    titleLarge = TextStyle(
        fontFamily = philosopherFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        fontStyle = FontStyle.Italic
    )
)