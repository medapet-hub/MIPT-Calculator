package com.mipt.calculator.ui.theme

import androidx.compose.ui.graphics.Color

// Windows Calculator–inspired Light palette
val CalcBackground      = Color(0xFFF3F3F3)  // Light grey background
val CalcSurface         = Color(0xFFFFFFFF)  // White digit buttons
val CalcSurfaceVariant  = Color(0xFFE6E6E6)  // Function & operator buttons (neutral grey)
val CalcPrimary         = Color(0xFF0067C0)  // Blue equals button (Windows accent)
val CalcOnPrimary       = Color(0xFFFFFFFF)
// Secondary tokens kept for active-operator state (dark grey highlight)
val CalcSecondary       = Color(0xFF4A4A4A)  // Active operator background
val CalcOnSecondary     = Color(0xFFFFFFFF)
// secondaryContainer no longer used for operator buttons; kept for theme completeness
val CalcSecondaryContainer   = Color(0xFFE6E6E6)
val CalcOnSecondaryContainer = Color(0xFF1A1A1A)
val CalcOnSurface       = Color(0xFF1A1A1A)
val CalcOnSurfaceVariant = Color(0xFF4A4A4A)
val CalcOutline         = Color(0xFFD0D0D0)
val CalcError           = Color(0xFFBA1A1A)
