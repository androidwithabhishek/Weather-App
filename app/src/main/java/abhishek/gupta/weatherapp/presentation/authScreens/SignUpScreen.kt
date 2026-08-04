package abhishek.gupta.weatherapp.presentation.authScreens

import abhishek.gupta.weatherapp.R
import abhishek.gupta.weatherapp.presentation.navigation.Routes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay




@Composable
fun SignUpScreen(authViewModel: AuthViewModel, navController: NavController) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var Triggeer by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isLoading2 by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Triggeer) {
        delay(3000)
        passwordVisible = !passwordVisible

    }

    val context = LocalContext.current



    val cornerShape = RoundedCornerShape(14.dp)

    // Email validation
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // Password validation
    val passwordRegex = Regex("^(?=.*[!@#\$%^&*(),.?\":{}|<>]).{6,10}\$")
    val isPasswordValid = passwordRegex.matches(password)

    // Enable button only if all fields valid
    val isFormValid = name.isNotBlank() && isEmailValid && isPasswordValid

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                buildAnnotatedString {
                    append("Create ")
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF6AC1FA), fontWeight = FontWeight.Bold
                        )
                    ) { append("Your Account") }
                    append(" to get started!")
                },
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 12.dp),
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(38.dp))

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { input ->
                    name = input.split(" ").joinToString(" ") { word ->
                        if (word.isNotEmpty()) word.replaceFirstChar { it.uppercase() }
                        else word
                    }
                },
                label = { Text("Full Name") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                modifier = Modifier.fillMaxWidth(),
                shape = cornerShape
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                modifier = Modifier.fillMaxWidth(),
                shape = cornerShape,
                isError = email.isNotEmpty() && !isEmailValid
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                modifier = Modifier.fillMaxWidth(),
                shape = cornerShape,
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                        Triggeer = !Triggeer
                    }) {
                        Icon(
                            painter = painterResource(
                                if (passwordVisible) R.drawable.baseline_visibility_24
                                else R.drawable.outline_visibility_off_24
                            ),
                            contentDescription = null
                        )
                    }
                },
                isError = password.isNotEmpty() && !isPasswordValid
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sign Up Button
            Button(
                onClick = {
                    if (isFormValid) {
                        isLoading = true
                        authViewModel.signUp(
                            email = email,
                            password = password,
                            name = name,
                            onResult = { message, success ->
                                if (success) {
                                    isLoading = true
                                    navController.navigate(Routes.HomeScreen)
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    isLoading = true
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }

                        )
                        isLoading = false

                    } else {
                        Toast.makeText(
                            context,
                            "Please fill all fields correctly",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6AC1FA)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = cornerShape
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .size(30.dp)
                    )
                } else {
                    Text(
                        "Sign Up",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            TextButton(
                onClick = {

                    isLoading2 = true

                    authViewModel.handleGoogleSignIn(
                        context = context,
                        onResult = { message, success ->
                            if (success) {
                                isLoading2 = false
                                navController.navigate(Routes.HomeScreen)
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                isLoading = false
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )


                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = cornerShape,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color(0xCBFFFFFF),
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) { if (isLoading2){
                CircularProgressIndicator()
            }else{
                Icon(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Continue with Google",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium, color = Color.Black
                    )
                )
            }

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Text

            TextButton(onClick = {

                navController.navigate(Routes.LogInScreen)
            }) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        ) { append("Already have an account? ") }

                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF6AC1FA),
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.Medium
                            )
                        ) { append("Log in") }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }




            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


