package com.example.togofood.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.togofood.R
import com.example.togofood.data.repository.FavoritesRepository
import com.example.togofood.data.repository.SessionRepository
import com.example.togofood.ui.components.BrandOrange
import com.example.togofood.ui.navigation.BottomNavItem
import com.example.togofood.ui.screens.favorites.FavoritesScreen
import com.example.togofood.ui.screens.home.HomeScreen
import com.example.togofood.ui.screens.map.MapScreen
import com.example.togofood.ui.screens.preparation.PreparationDetailScreen
import com.example.togofood.ui.screens.profile.ProfileScreen
import com.example.togofood.ui.screens.search.SearchScreen
import com.example.togofood.ui.screens.seller.SellerDetailScreen
import com.example.togofood.ui.screens.sellerhub.SellerHubScreen
import com.example.togofood.ui.screens.sellerhub.SellerPrepEditScreen
import com.example.togofood.ui.screens.sellerhub.SellerProfileEditScreen
import com.example.togofood.ui.screens.sellerhub.SellerScheduleScreen
import com.example.togofood.ui.theme.TogoMotion
import com.example.togofood.ui.theme.togoDetailEnter
import com.example.togofood.ui.theme.togoDetailExit
import com.example.togofood.ui.theme.togoDetailPopEnter
import com.example.togofood.ui.theme.togoDetailPopExit

private val NavSurface = Color(0xFFFAFAFA)
private val NavHairline = Color(0xFFE8E8E8)

@Composable
fun MainScreen(
    onLoginClick: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isMainTab = currentDestination?.route in BottomNavItem.all.map { it.route }

    val favPrepIds by FavoritesRepository.ids.collectAsState()
    val favSellerIds by FavoritesRepository.sellerIds.collectAsState()
    val favoritesCount = favPrepIds.size + favSellerIds.size
    val session by SessionRepository.session.collectAsState()
    val showVendorBadge = session.isVendor

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = isMainTab,
                enter = slideInVertically(TogoMotion.tweenNormal()) { it } + fadeIn(TogoMotion.tweenNormal()),
                exit = slideOutVertically(TogoMotion.tweenFast()) { it } + fadeOut(TogoMotion.tweenFast())
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(NavHairline)
                    )
                    NavigationBar(containerColor = NavSurface) {
                        BottomNavItem.all.forEach { item ->
                            val selected =
                                currentDestination?.hierarchy?.any { it.route == item.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    NavIcon(
                                        item = item,
                                        selected = selected,
                                        favoritesCount = favoritesCount,
                                        showVendorBadge = showVendorBadge,
                                    )
                                },
                                label = {
                                    Text(
                                        stringResource(item.labelRes),
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = BrandOrange,
                                    selectedTextColor = BrandOrange,
                                    indicatorColor = BrandOrange.copy(alpha = 0.12f),
                                    unselectedIconColor = Color(0xFF9E9E9E),
                                    unselectedTextColor = Color(0xFF9E9E9E)
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { TogoMotion.tabEnter() },
            exitTransition = { TogoMotion.tabExit() },
            popEnterTransition = { TogoMotion.tabEnter() },
            popExitTransition = { TogoMotion.tabExit() }
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onSellerClick = { sellerId ->
                        navController.navigate("seller_detail/$sellerId")
                    },
                    onPreparationClick = { preparationId ->
                        navController.navigate("preparation_detail/$preparationId")
                    },
                    onSearchClick = {
                        navController.navigate(BottomNavItem.Search.route)
                    },
                    onMapClick = {
                        navController.navigate("map")
                    }
                )
            }
            composable(BottomNavItem.Search.route) {
                SearchScreen(
                    onItemClick = { preparationId ->
                        navController.navigate("preparation_detail/$preparationId")
                    },
                    onSellerClick = { sellerId ->
                        navController.navigate("seller_detail/$sellerId")
                    }
                )
            }
            composable(BottomNavItem.Favorites.route) {
                FavoritesScreen(
                    onPreparationClick = { preparationId ->
                        navController.navigate("preparation_detail/$preparationId")
                    },
                    onSellerClick = { sellerId ->
                        navController.navigate("seller_detail/$sellerId")
                    },
                    onDiscoverClick = {
                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    onFavoritesClick = {
                        navController.navigate(BottomNavItem.Favorites.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onLoginClick = onLoginClick,
                    onSellerHubClick = {
                        navController.navigate("seller_hub")
                    }
                )
            }

            composable(
                route = "seller_hub",
                enterTransition = { togoDetailEnter() },
                exitTransition = { togoDetailExit() },
                popEnterTransition = { togoDetailPopEnter() },
                popExitTransition = { togoDetailPopExit() }
            ) {
                SellerHubScreen(
                    onBackClick = { navController.popBackStack() },
                    onEditProfileClick = { navController.navigate("seller_profile_edit") },
                    onScheduleClick = { navController.navigate("seller_schedule") },
                    onAddPrepClick = { navController.navigate("seller_prep_edit?prepId=") },
                    onEditPrepClick = { prepId ->
                        navController.navigate("seller_prep_edit?prepId=$prepId")
                    },
                    onViewPublicClick = { sellerId ->
                        navController.navigate("seller_detail/$sellerId")
                    },
                )
            }

            composable(
                route = "seller_profile_edit",
                enterTransition = { togoDetailEnter() },
                exitTransition = { togoDetailExit() },
                popEnterTransition = { togoDetailPopEnter() },
                popExitTransition = { togoDetailPopExit() }
            ) {
                SellerProfileEditScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "seller_schedule",
                enterTransition = { togoDetailEnter() },
                exitTransition = { togoDetailExit() },
                popEnterTransition = { togoDetailPopEnter() },
                popExitTransition = { togoDetailPopExit() }
            ) {
                SellerScheduleScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "seller_prep_edit?prepId={prepId}",
                arguments = listOf(
                    navArgument("prepId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                ),
                enterTransition = { togoDetailEnter() },
                exitTransition = { togoDetailExit() },
                popEnterTransition = { togoDetailPopEnter() },
                popExitTransition = { togoDetailPopExit() }
            ) {
                SellerPrepEditScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = "map",
                enterTransition = { TogoMotion.sheetEnter() },
                exitTransition = { TogoMotion.sheetExit() },
                popEnterTransition = { TogoMotion.tabEnter() },
                popExitTransition = { TogoMotion.sheetExit() }
            ) {
                MapScreen(
                    onBackClick = { navController.popBackStack() },
                    onSellerClick = { sellerId ->
                        navController.navigate("seller_detail/$sellerId")
                    }
                )
            }

            composable(
                route = "seller_detail/{sellerId}",
                enterTransition = { togoDetailEnter() },
                exitTransition = { togoDetailExit() },
                popEnterTransition = { togoDetailPopEnter() },
                popExitTransition = { togoDetailPopExit() }
            ) {
                SellerDetailScreen(
                    onBackClick = { navController.popBackStack() },
                    onPreparationClick = { preparationId ->
                        navController.navigate("preparation_detail/$preparationId")
                    }
                )
            }

            composable(
                route = "preparation_detail/{preparationId}",
                enterTransition = { togoDetailEnter() },
                exitTransition = { togoDetailExit() },
                popEnterTransition = { togoDetailPopEnter() },
                popExitTransition = { togoDetailPopExit() }
            ) {
                PreparationDetailScreen(
                    onBackClick = { navController.popBackStack() },
                    onSellerClick = { sellerId ->
                        navController.navigate("seller_detail/$sellerId")
                    }
                )
            }
        }
    }
}

@Composable
private fun NavIcon(
    item: BottomNavItem,
    selected: Boolean,
    favoritesCount: Int,
    showVendorBadge: Boolean,
) {
    val icon = if (selected) item.selectedIcon else item.unselectedIcon
    val label = stringResource(item.labelRes)

    when (item) {
        BottomNavItem.Favorites -> {
            if (favoritesCount > 0) {
                BadgedBox(
                    badge = {
                        Badge(containerColor = BrandOrange) {
                            Text(
                                text = if (favoritesCount > 99) "99+" else favoritesCount.toString(),
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }
                    }
                ) {
                    Icon(imageVector = icon, contentDescription = label)
                }
            } else {
                Icon(imageVector = icon, contentDescription = label)
            }
        }
        BottomNavItem.Profile -> {
            if (showVendorBadge) {
                BadgedBox(
                    badge = {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(BrandOrange)
                        )
                    }
                ) {
                    Icon(imageVector = icon, contentDescription = label)
                }
            } else {
                Icon(imageVector = icon, contentDescription = label)
            }
        }
        else -> Icon(imageVector = icon, contentDescription = label)
    }
}
