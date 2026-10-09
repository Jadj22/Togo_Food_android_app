package com.example.togofood.ui.components

/** Formats an Int price as a spaced FCFA string, e.g. 2000 → "2 000". */
fun Int.formatFcfa(): String =
    "%,d".format(java.util.Locale.US, this).replace(",", " ")

/** Formats a distance in meters: < 1 km → "380 m", >= 1 km → "1.1 km". */
fun Int.formatDistance(): String =
    if (this < 1000) "$this m" else "${"%.1f".format(java.util.Locale.US, this / 1000f)} km"
