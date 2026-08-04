package abhishek.gupta.weatherapp.presentation.authScreens


import abhishek.gupta.weatherapp.data.remote.supabase.SupabaseClientProvider
import android.content.Context
import android.util.Log
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.firebase.firestore.ListenerRegistration
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.delay

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {


    private val _currentUserData = MutableStateFlow(GetUserInfo())
    val currentUserData: StateFlow<GetUserInfo> = _currentUserData

    val db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()


    fun logoutUser() {
        viewModelScope.launch {
            delay(1000)
            auth.signOut()
        }
    }


    fun signUp(
        email: String,
        password: String,
        name: String,
        onResult: (String, Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val user = auth.currentUser
                        val userId = user?.uid

                        if (userId != null) {
                            val userInfo = PostUserInfo(
                                profileImageUrl = "",
                                name = name,
                                email = email,
                                uid = userId,
                                passkey = password,
                                likedCity = emptyList(),
                            )

                            db.collection("user").document(userId).set(userInfo)
                                .addOnSuccessListener {
                                    onResult("Signup successful", true)
                                }.addOnFailureListener { exception ->
                                    auth.currentUser?.delete() // rollback user creation
                                    onResult("Failed to save user info", false)
                                }
                        } else {
                            onResult("User ID not found", false)
                        }
                    } else {
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthUserCollisionException -> "This email is already registered"
                            is FirebaseAuthWeakPasswordException -> "Password is too weak"
                            else -> task.exception?.localizedMessage ?: "Signup failed"
                        }
                        onResult("mine $errorMessage ", false)
                    }
                }
            } catch (e: Exception) {
                onResult("Unexpected error: ${e.localizedMessage}", false)
            }
        }
    }


    fun logIn(
        email: String,
        passkey: String,
        onResult: (String, Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, passkey).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult("Login successful", true)
                    } else {
                        val errorMessage = task.exception?.localizedMessage ?: "Login failed"
                        onResult(errorMessage, false)
                    }
                }
            } catch (e: Exception) {
                onResult("Error: ${e.localizedMessage}", false)
            }
        }
    }

    fun handleGoogleSignIn(
        context: Context,
        onResult: (String, Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            googleSignIn(context).collect { result ->
                result.fold(onSuccess = { authResult ->
                    val currentUser = authResult.user
                    if (currentUser != null) {
                        val postUserInfo = PostUserInfo(
                            profileImageUrl = currentUser.photoUrl?.toString() ?: "",
                            name = currentUser.displayName ?: "",
                            email = currentUser.email ?: "",
                            uid = currentUser.uid,
                            passkey = "",
                            likedCity = emptyList(),
                        )

                        db.collection("user").document(currentUser.uid).set(postUserInfo)
                            .addOnSuccessListener {
                                onResult("Signup successful", true)
                            }.addOnFailureListener { exception ->
                                onResult(
                                    "Failed to save user info: ${exception.localizedMessage}", false
                                )
                            }
                    } else {
                        onResult("Google sign-in failed: user is null", false)
                    }
                }, onFailure = { e ->
                    onResult("Google sign-in failed: ${e.localizedMessage}", false)
                })
            }
        }
    }


    private suspend fun googleSignIn(context: Context): Flow<Result<AuthResult>> {


        return callbackFlow {
            try {
                val credentialManager = CredentialManager.create(context)

                val googleIdOption =
                    GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
                        .setServerClientId("755761298478-9og1v0q664g16s3curmsros55pidbb2q.apps.googleusercontent.com")
                        .setAutoSelectEnabled(true).build()

                val request =
                    GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

                val result = credentialManager.getCredential(context, request)
                val credential = result.credential

                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    val authCredential =
                        GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                    val authResult = auth.signInWithCredential(authCredential).await()
                    trySend(Result.success(authResult))
                } else {
                    trySend(Result.failure(Exception("Invalid credential type.")))
                }
            } catch (e: GetCredentialCancellationException) {
                trySend(Result.failure(Exception("Sign-in was canceled.")))
            } catch (e: Exception) {
                trySend(Result.failure(e))
            }

            awaitClose { close() }
        }
    }





    private var userListenerRegistration: ListenerRegistration? = null


    fun fetchCurrentUserData() {
        val userId = auth.currentUser?.uid ?: return
        userListenerRegistration?.remove()
        userListenerRegistration =
            db.collection("user").document(userId).addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("AuthViewModel", "fetchCurrentDonerData listen failed", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.toObject(GetUserInfo::class.java)
                    if (data != null && data != _currentUserData.value) {
                        _currentUserData.value = data
                    }
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        userListenerRegistration?.remove()
    }


    fun updateProfileImage(
        imageBytes: ByteArray,
        onResult: (String, Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val imageFileName = "profile_images/$userId.jpg"
            try {
                val imageBucket = SupabaseClientProvider.client.storage["weather_app_pfp"]
                imageBucket.upload(imageFileName, imageBytes, upsert = true)
                val profileImageUrl = "${imageBucket.publicUrl(imageFileName)}?v=${System.currentTimeMillis()}"

                firestore.collection("user").document(userId)
                    .update("profileImageUrl", profileImageUrl)
                    .addOnSuccessListener {
                        onResult("Profile picture updated", true)
                    }
                    .addOnFailureListener { e ->
                        onResult(e.toString(), false)
                    }
            } catch (e: Exception) {
                onResult(e.toString(), false)
                Log.e("lol", e.localizedMessage.toString())
            }
        }
    }

    fun updateProfileName(
        name: String,
        onResult: (String, Boolean) -> Unit,
    ) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            try {
                firestore.collection("user").document(userId)
                    .update("name", name)
                    .addOnSuccessListener {
                        onResult("Name updated", true)
                    }
                    .addOnFailureListener { e ->
                        onResult(e.toString(), false)
                    }
            } catch (e: Exception) {
                onResult(e.toString(), false)
                Log.e("lol", e.localizedMessage.toString())
            }
        }
    }

}


data class PostUserInfo(
    val profileImageUrl: String,
    val name: String,
    val email: String,
    val uid: String,
    val passkey: String,
    val likedCity: List<String>,
)

data class GetUserInfo(
    val profileImageUrl: String = "",
    val name: String = "",
    val email: String = "",
    val uid: String = "",
    val passkey: String = "",
    val likedCity: List<String> = emptyList<String>(),
)
