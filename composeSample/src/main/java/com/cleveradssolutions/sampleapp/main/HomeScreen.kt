package com.cleveradssolutions.sampleapp.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.ui.theme.CASAndroidTheme

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        Button(
            onClick = { navController.navigate(NavDestinations.Banner.name) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(context.getString(R.string.nav_banner))
        }
        Button(
            onClick = { navController.navigate(NavDestinations.LazyBanner.name) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(context.getString(R.string.nav_lazy_banner))
        }
        Button(
            onClick = { navController.navigate(NavDestinations.Native.name) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(context.getString(R.string.nav_native))
        }
        Button(
            onClick = { navController.navigate(NavDestinations.NativeTemplate.name) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(context.getString(R.string.nav_native_template))
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    CASAndroidTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            HomeScreen(rememberNavController())
        }
    }
}
