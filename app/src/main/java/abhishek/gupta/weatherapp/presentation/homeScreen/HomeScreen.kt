package abhishek.gupta.weatherapp.presentation.homeScreen


import abhishek.gupta.weatherapp.R
import abhishek.gupta.weatherapp.presentation.authScreens.AuthViewModel
import abhishek.gupta.weatherapp.presentation.utilsScreens.FavPage
import abhishek.gupta.weatherapp.presentation.utilsScreens.HomePage
import abhishek.gupta.weatherapp.presentation.utilsScreens.ProfilePage

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               authViewModel: AuthViewModel,
               navController: NavHostController,
               homeViewModel: HomeViewmodel,) {

    val navItems = listOf(
        NavItems(
            "Favorite",
            filledIcon = Icons.Filled.Favorite,
            outlinedIcon = Icons.Outlined.FavoriteBorder
        ),
        NavItems(
            "Home",
            filledIcon = Icons.Filled.Home,
            outlinedIcon = Icons.Outlined.Home
        ),
        NavItems(
            "Profile",
            filledIcon = Icons.Filled.AccountCircle,
            outlinedIcon = Icons.Outlined.AccountCircle
        )
    )

    var selectedIndex by rememberSaveable { mutableIntStateOf(1) }


    Scaffold(modifier.fillMaxSize(), bottomBar = {
        NavigationBar(modifier.height(70.dp), containerColor = colorResource(id = R.color.app)) {

            navItems.forEachIndexed(){

                index, navItem ->
                val isSelected = selectedIndex == index

                NavigationBarItem(
                    modifier = Modifier.offset(y = 10.dp),
                    selected = false,
                    onClick = {
                        selectedIndex = index
                    },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) navItem.filledIcon else navItem.outlinedIcon,
                            contentDescription = null,
                            tint = Color(0xFF000000)

                        )
                    },
                    label = {
                        Text(
                            text = navItem.title,
                            modifier = Modifier.offset(y = (-4).dp),
                            color = Color(0xFF000000)
                        )
                    }
                )
            }


        }
    }) { innerPadding ->




        MaterialTheme {

            when(selectedIndex){


                0 ->{
                    FavPage(
                        homeViewModel = homeViewModel,
                        authViewModel = authViewModel,
                        navController = navController
                    )
                }

                1 -> {
                    HomePage(
                        modifier = Modifier.padding(innerPadding),
                        homeViewmodel = homeViewModel,
                        authViewModel = authViewModel,
                        navController = navController
                    )
                }

                2 ->{
                    ProfilePage(
                        authViewModel = authViewModel,
                        homeViewmodel = homeViewModel
                    )
                }

            }


        }


    }
}



data class NavItems(val title: String, val filledIcon: ImageVector, val outlinedIcon: ImageVector)