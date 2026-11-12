package com.mipt.calculator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CalculatorLightColorScheme = lightColorScheme(
    primary              = CalcPrimary,
    onPrimary            = CalcOnPrimary,
    secondary            = CalcSecondary,
    onSecondary          = CalcOnSecondary,
    secondaryContainer   = CalcSecondaryContainer,
    onSecondaryContainer = CalcOnSecondaryContainer,
    background           = CalcBackground,
    surface              = CalcSurface,
    surfaceVariant       = CalcSurfaceVariant,
    onSurface            = CalcOnSurface,
    onSurfaceVariant     = CalcOnSurfaceVariant,
    outline              = CalcOutline,
    error                = CalcError
)

@Composable
fun MIPT_CalculatorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CalculatorLightColorScheme,
        typography = Typography,
        content = content
    )
}
