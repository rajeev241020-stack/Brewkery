package com.intern.brewkeryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.intern.brewkeryapp.UX.Cream
import com.intern.brewkeryapp.UX.Screen.CartScreen
import com.intern.brewkeryapp.UX.Screen.ItemDetailScreen
import com.intern.brewkeryapp.UX.Screen.MenuScreen
import com.intern.brewkeryapp.UX.Screen.OrderStatusScreen
import com.intern.brewkeryapp.ViewModel.BrewViewModel
import com.intern.brewkeryapp.ui.theme.BrewkeryAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrewkeryAppTheme {
                    Surface(Modifier.fillMaxSize(), color = Cream) {
                        BrewkeryApp()
                    }
                }
            }
        }
    }

    object Routes {
        const val MENU = "menu"
        const val DETAIL = "detail/{id}"
        const val CART = "cart"
        const val ORDER = "order"
        fun detail(id: Int) = "detail/$id"
    }

    @androidx.compose.runtime.Composable
    fun BrewkeryApp() {
        val nav = rememberNavController()
        val vm: BrewViewModel = viewModel()

        NavHost(navController = nav, startDestination = Routes.MENU) {
            composable(Routes.MENU) {
                MenuScreen(
                    vm = vm,
                    onItemClick = { nav.navigate(Routes.detail(it)) },
                    onCartClick = { nav.navigate(Routes.CART) },
                    onTrackOrder = { nav.navigate(Routes.ORDER) }
                )
            }
            composable(
                Routes.DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                ItemDetailScreen(
                    vm = vm,
                    itemId = entry.arguments?.getInt("id") ?: 0,
                    onBack = { nav.popBackStack() },
                    onAdded = { nav.navigate(Routes.CART) { popUpTo(Routes.MENU) } }
                )
            }
            composable(Routes.CART) {
                CartScreen(
                    vm = vm,
                    onBack = { nav.popBackStack() },
                    onOrderPlaced = {
                        nav.navigate(Routes.ORDER) { popUpTo(Routes.MENU) }
                    }
                )
            }
            composable(Routes.ORDER) {
                OrderStatusScreen(
                    vm = vm,
                    onBackToMenu = { nav.popBackStack(Routes.MENU, inclusive = false) }
                )
            }
        }
    }

