package com.zonealarm.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * ZoneAlarm design system — color tokens for Light and Dark themes.
 * Amber primary + Indigo accent on Slate surfaces.
 *
 * Auto-adjusts with system night mode.
 */

// ── Primary (Amber) ──
val Amber800 = Color(0xFF92400E)
val Amber700 = Color(0xFFB45309) // high-contrast primary for light mode (4.6:1+ on white)
val Amber600 = Color(0xFFD97706)
val Amber500 = Color(0xFFF59E0B) // vibrant primary for dark mode (9.6:1+ on dark slate)
val Amber400 = Color(0xFFFBBF24)
val Amber100 = Color(0xFFFEF3C7) // warm light container
val Amber900 = Color(0xFF78350F) // warm dark container
val OnAmberDark = Color(0xFF1C1917) // dark text on bright amber
val OnAmberLight = Color(0xFFFFFFFF) // white text on deep amber

// ── Accent (Indigo) ──
val Indigo700 = Color(0xFF4338CA)
val Indigo600 = Color(0xFF4F46E5) // secondary for light mode
val Indigo500 = Color(0xFF6366F1)
val Indigo400 = Color(0xFF818CF8) // secondary for dark mode
val Indigo100 = Color(0xFFE0E7FF)
val Indigo900 = Color(0xFF312E81)
val OnIndigo = Color(0xFFFFFFFF)

// ── Slate Scale ──
val Slate950 = Color(0xFF020617) // dark background
val Slate900 = Color(0xFF0F172A) // dark surface
val Slate800 = Color(0xFF1E293B) // dark surface-variant / dark border
val Slate700 = Color(0xFF334155) // dark outline
val Slate600 = Color(0xFF475569) // light secondary text
val Slate500 = Color(0xFF64748B) // muted text
val Slate400 = Color(0xFF94A3B8) // dark secondary text
val Slate300 = Color(0xFFCBD5E1) // light outline
val Slate200 = Color(0xFFE2E8F0) // light border
val Slate100 = Color(0xFFF1F5F9) // light surface-variant / dark primary text
val Slate50  = Color(0xFFF8FAFC) // light background

// ── Semantic ──
val Success = Color(0xFF22C55E)
val Error = Color(0xFFEF4444)
val Warning = Color(0xFFF59E0B)
val Info = Color(0xFF3B82F6)
