package com.example.togofood.ui.theme

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Specs d'animation partagées — une seule "voix" motion pour toute l'app.
 * Courtes, spring doux, easing Material : sensation fluide sans lourdeur.
 */
object TogoMotion {
    const val FastMs = 180
    const val NormalMs = 300
    const val SlowMs = 420

    fun <T> tweenFast(): TweenSpec<T> = tween(FastMs, easing = FastOutSlowInEasing)
    fun <T> tweenNormal(): TweenSpec<T> = tween(NormalMs, easing = FastOutSlowInEasing)
    fun <T> tweenSlow(): TweenSpec<T> = tween(SlowMs, easing = FastOutSlowInEasing)

    val SoftSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
    val PressSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
    val HeartSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    /** Tabs bottom nav : fade croisé léger (pas de slide latéral). */
    fun tabEnter(): EnterTransition = fadeIn(tweenNormal()) + scaleIn(
        initialScale = 0.98f,
        animationSpec = tweenNormal()
    )
    fun tabExit(): ExitTransition = fadeOut(tweenFast()) + scaleOut(
        targetScale = 0.98f,
        animationSpec = tweenFast()
    )

    /** Détail (plat / vendeur / carte) : slide depuis la droite + fade. */
    fun detailEnter(): EnterTransition =
        slideInHorizontally(tweenNormal()) { it / 5 } + fadeIn(tweenNormal())
    fun detailExit(): ExitTransition =
        slideOutHorizontally(tweenNormal()) { -it / 8 } + fadeOut(tweenFast())
    fun detailPopEnter(): EnterTransition =
        slideInHorizontally(tweenNormal()) { -it / 8 } + fadeIn(tweenNormal())
    fun detailPopExit(): ExitTransition =
        slideOutHorizontally(tweenNormal()) { it / 5 } + fadeOut(tweenFast())

    /** Modal / map plein écran : monte depuis le bas. */
    fun sheetEnter(): EnterTransition =
        slideInVertically(tweenSlow()) { it / 4 } + fadeIn(tweenNormal())
    fun sheetExit(): ExitTransition =
        slideOutVertically(tweenNormal()) { it / 4 } + fadeOut(tweenFast())

    /** Contenu interne (filtres Home, empty → list…). */
    fun contentSwitch(): ContentTransform =
        (fadeIn(tweenNormal()) + slideInVertically(tweenNormal()) { it / 24 }) togetherWith
            (fadeOut(tweenFast()) + slideOutVertically(tweenFast()) { -it / 24 })
}

/**
 * Apparition douce au premier composition (sections, headers).
 * Évite le "pop" brut au chargement.
 */
fun Modifier.togoEnter(
    delayMs: Int = 0,
    offsetY: Float = 16f
): Modifier = composed {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val alpha by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(TogoMotion.NormalMs, delayMillis = delayMs, easing = FastOutSlowInEasing),
        label = "togoEnterAlpha"
    )
    val ty by animateFloatAsState(
        targetValue = if (shown) 0f else offsetY,
        animationSpec = tween(TogoMotion.NormalMs, delayMillis = delayMs, easing = FastOutSlowInEasing),
        label = "togoEnterY"
    )
    graphicsLayer {
        this.alpha = alpha
        translationY = ty
    }
}

/** Helpers typés pour NavHost AnimatedContentTransitionScope. */
fun AnimatedContentTransitionScope<*>.togoDetailEnter(): EnterTransition = TogoMotion.detailEnter()
fun AnimatedContentTransitionScope<*>.togoDetailExit(): ExitTransition = TogoMotion.detailExit()
fun AnimatedContentTransitionScope<*>.togoDetailPopEnter(): EnterTransition = TogoMotion.detailPopEnter()
fun AnimatedContentTransitionScope<*>.togoDetailPopExit(): ExitTransition = TogoMotion.detailPopExit()
