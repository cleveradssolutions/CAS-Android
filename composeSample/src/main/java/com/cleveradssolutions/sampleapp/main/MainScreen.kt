package com.cleveradssolutions.sampleapp.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.formats.BannerScreen
import com.cleveradssolutions.sampleapp.formats.LazyBannerScreen
import com.cleveradssolutions.sampleapp.formats.NativeScreen
import com.cleveradssolutions.sampleapp.formats.NativeTemplateScreen
import com.cleveradssolutions.sampleapp.ui.theme.CASAndroidTheme


@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var showNavigationIcon by remember { mutableStateOf(false) }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            showNavigationIcon = destination.route != NavDestinations.Home.name
        }
    }

    Scaffold(
        topBar = {
            MainTopBar(
                isNavigationEnabled = showNavigationIcon,
                navigateBack = { navController.popBackStack() },
                modifier = modifier,
            )
        },
        contentWindowInsets =
            WindowInsets.systemBars.only(WindowInsetsSides.Vertical + WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = NavDestinations.Home.name) {
                composable(NavDestinations.Home.name) { HomeScreen( navController) }
                composable(NavDestinations.Banner.name) { BannerScreen() }
                composable(NavDestinations.LazyBanner.name) { LazyBannerScreen() }
                composable(NavDestinations.Native.name) { NativeScreen() }
                composable(NavDestinations.NativeTemplate.name) { NativeTemplateScreen() }
            }
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(
    isNavigationEnabled: Boolean,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    TopAppBar(
        modifier = modifier,
        title = { Text(context.getString(R.string.main_title)) },
        navigationIcon = {
            if (isNavigationEnabled) {
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            }
        },
    )
}


@Preview
@Composable
private fun MainScreenPreview() {
    CASAndroidTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen()
        }
    }
}

