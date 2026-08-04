package abhishek.gupta.weatherapp.presentation.utilsScreens

import abhishek.gupta.weatherapp.R
import abhishek.gupta.weatherapp.data.converter.uriToByteArray
import abhishek.gupta.weatherapp.presentation.authScreens.AuthViewModel
import abhishek.gupta.weatherapp.presentation.homeScreen.HomeViewmodel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.coroutines.time.delay


private fun String.toTitleCase(): String =
    trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { c -> c.uppercase() }
        }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    homeViewmodel: HomeViewmodel,
) {

    LaunchedEffect(Unit) {
        authViewModel.fetchCurrentUserData()
    }

    val context = LocalContext.current
    val currentUser by authViewModel.currentUserData.collectAsState()

    var isEditingName by rememberSaveable { mutableStateOf(false) }
    var newName by rememberSaveable { mutableStateOf(currentUser.name) }
    var isSavingName by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(currentUser.name, isEditingName) {
        if (!isEditingName) newName = currentUser.name
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isSavingImage by remember { mutableStateOf(false) }


    val imageRequest = remember(currentUser.profileImageUrl) {
        ImageRequest.Builder(context)
            .data(currentUser.profileImageUrl)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
    }
    val painter = rememberAsyncImagePainter(model = imageRequest)
    val imageState by painter.state.collectAsState()

    fun saveImage(uri: Uri) {
        isSavingImage = true
        val bytes = uri.uriToByteArray(context)
        if (bytes == null) {
            isSavingImage = false
            selectedImageUri = null
            Toast.makeText(context, "Could not read image", Toast.LENGTH_SHORT).show()
            return
        }
        authViewModel.updateProfileImage(
            imageBytes = bytes,
            onResult = { message, success ->
                isSavingImage = false
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                selectedImageUri = null
                if (success) {
                    authViewModel.fetchCurrentUserData()
                }
            }
        )
    }


    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                selectedImageUri = uri
                saveImage(uri)
            } else {
                Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val uri = result.data?.data
            if (result.resultCode == Activity.RESULT_OK && uri != null) {
                selectedImageUri = uri
                saveImage(uri)
            } else {
                Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    )

    fun openImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } else {
            galleryLauncher.launch(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
            )
        }
    }

    fun saveName() {
        val formatted = newName.toTitleCase()
        if (formatted.isBlank() || formatted == currentUser.name) {
            isEditingName = false
            return
        }
        isSavingName = true
        authViewModel.updateProfileName(
            name = formatted,
            onResult = { message, success ->
                isSavingName = false
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                if (success) {
                    isEditingName = false
                    authViewModel.fetchCurrentUserData()
                }
            }
        )
    }

    var showDialogLogout by rememberSaveable { mutableStateOf(false) }
    var showDialogClearCache by rememberSaveable { mutableStateOf(false) }
    var showDialogNotification by rememberSaveable { mutableStateOf(false) }
    var isDeletingCache by rememberSaveable { mutableStateOf(false) }

    var isNotificationEnabled by remember { mutableStateOf(isAppNotificationEnabled(context)) }


    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isNotificationEnabled = isAppNotificationEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }


    val appColor = colorResource(id = R.color.app)
    val dangerColor = Color(0xFFD64545)
    val cardShape = RoundedCornerShape(20.dp)
    val screenHPadding = 20.dp

    val cardColor = MaterialTheme.colorScheme.surfaceContainerHigh

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile", modifier.padding(start = 6.dp),
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appColor)
            )
        }
    ) { innerPadding ->


        if (showDialogLogout) {
            AlertDialog(
                onDismissRequest = { showDialogLogout = false },
                icon = {
                    Icon(
                        Icons.Outlined.Logout,
                        contentDescription = null,
                        tint = dangerColor
                    )
                },
                title = { Text("Log out") },
                text = { Text("Are you sure you want to log out of your account?") },
                confirmButton = {
                    TextButton(onClick = {

                        showDialogLogout = true

                        homeViewmodel.clearAppDataSafely { _, message ->
                            isDeletingCache = false
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            showDialogClearCache = false
                        }

                        authViewModel.logoutUser()

                        showDialogLogout = false
                    }) { Text("Log out", color = dangerColor, fontWeight = FontWeight.SemiBold) }
                },
                dismissButton = {
                    TextButton(onClick = { showDialogLogout = false }) { Text("Cancel") }
                }
            )
        }

        if (showDialogClearCache) {
            AlertDialog(
                onDismissRequest = { if (!isDeletingCache) showDialogClearCache = false },
                icon = { Icon(Icons.Default.CleaningServices, contentDescription = null) },
                title = { Text("Clear cache") },
                text = {
                    if (isDeletingCache) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Text("Clearing cache, please wait…")
                        }
                    } else {
                        Text("This will remove temporarily stored app data. This action cannot be undone.")
                    }
                },
                confirmButton = {
                    if (!isDeletingCache) {
                        TextButton(onClick = {
                            isDeletingCache = true
                            homeViewmodel.clearAppDataSafely { _, message ->
                                isDeletingCache = false
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                showDialogClearCache = false
                            }
                        }) { Text("Clear") }
                    }
                },
                dismissButton = {
                    if (!isDeletingCache) {
                        TextButton(onClick = {


                            showDialogClearCache = false


                        }) { Text("Cancel") }
                    }
                }
            )
        }

        if (showDialogNotification) {
            AlertDialog(
                onDismissRequest = { showDialogNotification = false },
                icon = { Icon(Icons.Default.NotificationsNone, contentDescription = null) },
                title = { Text("Notifications") },
                text = {
                    Text(
                        if (isNotificationEnabled) "Notifications are currently ON. Turn them OFF?"
                        else "Notifications are currently OFF. Turn them ON?"
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val intent = Intent().apply {
                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                        showDialogNotification = false
                    }) { Text(if (isNotificationEnabled) "Turn off" else "Turn on") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialogNotification = false }) { Text("Cancel") }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {


            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(
                            Brush.verticalGradient(listOf(appColor, appColor.copy(alpha = 0.75f)))
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(112.dp)
                                .shadow(elevation = 8.dp, shape = CircleShape, clip = false)
                                .clip(CircleShape)
                                .border(4.dp, MaterialTheme.colorScheme.background, CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable(enabled = !isSavingImage) { openImagePicker() },
                            contentAlignment = Alignment.Center
                        ) {
                            when {

                                selectedImageUri != null -> AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Profile Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )

                                imageState is AsyncImagePainter.State.Loading -> {
                                    AsyncImage(
                                        model = R.drawable.pf,
                                        contentDescription = "Profile Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                    CircularProgressIndicator(
                                        color = appColor,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                imageState is AsyncImagePainter.State.Success -> AsyncImage(
                                    model = imageRequest,
                                    contentDescription = "Profile Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )

                                else -> AsyncImage(
                                    model = R.drawable.pf,
                                    contentDescription = "Profile Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }


                            if (isSavingImage) {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color.Black.copy(alpha = 0.35f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }


                        FilledIconButton(
                            onClick = { openImagePicker() },
                            enabled = !isSavingImage,
                            modifier = Modifier.size(32.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = appColor)
                        ) {
                            Icon(
                                Icons.Default.PhotoCamera,
                                contentDescription = "Change photo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))


                    AnimatedContent(
                        targetState = isEditingName,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "name_edit_toggle"
                    ) { editing ->
                        if (!editing) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUser.name.toTitleCase()
                                        .ifBlank { "Add your name" },
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { isEditingName = true },
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit name",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = screenHPadding)
                            ) {
                                OutlinedTextField(
                                    value = newName,
                                    onValueChange = { newName = it },
                                    singleLine = true,
                                    enabled = !isSavingName,
                                    placeholder = { Text("Your name") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            isEditingName = false; newName = currentUser.name
                                        },
                                        enabled = !isSavingName
                                    ) { Text("Cancel") }
                                    Button(
                                        onClick = { saveName() },
                                        enabled = !isSavingName,
                                        colors = ButtonDefaults.buttonColors(containerColor = appColor)
                                    ) {
                                        if (isSavingName) {
                                            CircularProgressIndicator(
                                                color = Color.White,
                                                strokeWidth = 2.dp,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        } else {
                                            Text("Save")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentUser.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SectionLabel("PREFERENCES", screenHPadding)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = screenHPadding),
                color = cardColor,
                shape = cardShape,
                shadowElevation = 0.dp,
                tonalElevation = 1.dp
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.NotificationsNone,
                        title = "Notifications",
                        subtitle = if (isNotificationEnabled) "On" else "Off",
                        onClick = { showDialogNotification = true }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.6.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                    SettingsRow(
                        icon = Icons.Default.CleaningServices,
                        title = "Clear cache",
                        subtitle = "Free up storage used by the app",
                        onClick = { showDialogClearCache = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            SectionLabel("ACCOUNT ACTIONS", screenHPadding)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = screenHPadding),
                color = cardColor,
                shape = cardShape,
                shadowElevation = 0.dp,
                tonalElevation = 1.dp
            ) {
                SettingsRow(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Log out",
                    subtitle = null,
                    titleColor = dangerColor,
                    iconTint = dangerColor,
                    showChevron = false,
                    onClick = { showDialogLogout = true }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Composable
private fun SectionLabel(text: String, horizontalPadding: androidx.compose.ui.unit.Dp) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .padding(horizontal = horizontalPadding + 4.dp)
            .padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    showChevron: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = titleColor,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (showChevron) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

fun isAppNotificationEnabled(context: Context): Boolean {
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}