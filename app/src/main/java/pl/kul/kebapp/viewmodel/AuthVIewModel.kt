package pl.kul.kebapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.kul.kebapp.R

class AuthViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow(firebaseAuth.currentUser != null)
    val authState: StateFlow<Boolean> = _authState

    //Role
    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    init {
        _authState.value = firebaseAuth.currentUser != null
        if (_authState.value) {
            loadUserRole()
        }
    }

    fun login(
        context: Context,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onResult(false, context.getString(R.string.email_or_password_cannot_be_empty))
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = true
                    loadUserRole()//wczytujemy role
                    onResult(true, null)
                } else {
                    val message = mapFirebaseAuthError(context, task.exception)
                    onResult(false, message)
                }
            }
    }

    fun register(
        context: Context,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onResult(false, context.getString(R.string.email_or_password_cannot_be_empty))
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    _authState.value = true

                    //Dodajemy dane do Firestore
                    user?.let {
                        val db = FirebaseFirestore.getInstance()
                        val userDoc = mapOf(
                            "email" to email,
                            "role" to "user", // domyślna rola
                            "favorites" to emptyList<String>()
                        )

                        db.collection("users")
                            .document(user.uid)
                            .set(userDoc)
                            .addOnSuccessListener {
                                onResult(true, null)
                            }
                            .addOnFailureListener { e ->
                                onResult(
                                    false,
                                    "User created but Firestore failed: ${e.localizedMessage}"
                                )
                            }
                    } ?: onResult(false, "User registered, but user data is null")
                } else {
                    val message = mapFirebaseAuthError(context, task.exception)
                    onResult(false, message)
                }
            }
    }

    fun resetPassword(context: Context, email: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank()) {
            onResult(false, context.getString(R.string.email_cannot_be_empty))
            return
        }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    val message = mapFirebaseAuthError(context, task.exception)
                    onResult(false, message)
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
        _authState.value = false
    }

    private fun mapFirebaseAuthError(context: Context, exception: Exception?): String {
        if (exception == null) {
            return context.getString(R.string.unknown_error)
        }

        return when (exception) {
            is com.google.firebase.FirebaseNetworkException -> {
                context.getString(R.string.network_error)
            }

            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_INVALID_CUSTOM_TOKEN" -> context.getString(R.string.invalid_custom_token)
                    "ERROR_CUSTOM_TOKEN_MISMATCH" -> context.getString(R.string.custom_token_mismatch)
                    "ERROR_INVALID_CREDENTIAL" -> context.getString(R.string.invalid_credentials)
                    "ERROR_USER_MISMATCH" -> context.getString(R.string.user_mismatch)
                    "ERROR_REQUIRES_RECENT_LOGIN" -> context.getString(R.string.requires_recent_login)
                    "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> context.getString(R.string.account_exists_with_different_credential)
                    "ERROR_CREDENTIAL_ALREADY_IN_USE" -> context.getString(R.string.credential_already_in_use)
                    "ERROR_INVALID_EMAIL" -> context.getString(R.string.invalid_email_format)
                    "ERROR_WRONG_PASSWORD" -> context.getString(R.string.invalid_password)
                    "ERROR_USER_NOT_FOUND" -> context.getString(R.string.user_not_found)
                    "ERROR_USER_DISABLED" -> context.getString(R.string.user_disabled)
                    "ERROR_TOO_MANY_REQUESTS" -> context.getString(R.string.too_many_requests)
                    "ERROR_OPERATION_NOT_ALLOWED" -> context.getString(R.string.operation_not_allowed)
                    "ERROR_EMAIL_ALREADY_IN_USE" -> context.getString(R.string.email_already_in_use)
                    "ERROR_WEAK_PASSWORD" -> context.getString(R.string.weak_password)
                    "ERROR_INVALID_VERIFICATION_CODE" -> context.getString(R.string.invalid_verification_code)
                    "ERROR_INVALID_VERIFICATION_ID" -> context.getString(R.string.invalid_verification_id)
                    "ERROR_RECAPTCHA_NOT_READY" -> context.getString(R.string.recaptcha_not_ready)
                    "ERROR_NETWORK_REQUEST_FAILED" -> context.getString(R.string.network_error)
                    else -> context.getString(R.string.unknown_error) + ": ${exception.localizedMessage}"
                }
            }

            else -> {
                context.getString(R.string.unknown_error) + ": ${exception.localizedMessage}"
            }
        }
    }

    //Role
    private fun loadUserRole() {
        val user = firebaseAuth.currentUser ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role")
                _userRole.value = role
            }
            .addOnFailureListener {
                _userRole.value = null // albo np. "user", jako fallback
            }
    }
}
