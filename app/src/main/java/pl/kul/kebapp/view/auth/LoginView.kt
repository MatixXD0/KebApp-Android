package pl.kul.kebapp.view.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.kul.kebapp.R
import pl.kul.kebapp.view.auth.components.AuthHeader
import pl.kul.kebapp.view.auth.components.EmailTextField
import pl.kul.kebapp.view.auth.components.PasswordTextField
import pl.kul.kebapp.viewmodel.AuthViewModel

@Composable
fun LoginView(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthHeader(text = stringResource(R.string.log_in))

            EmailTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                viewModel.login(context, email, password) { success, message ->
                    if (!success) {
                        Toast.makeText(
                            context,
                            message ?: context.getString(R.string.login_or_password_incorrect),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }) {
                Text(stringResource(R.string.log_in))
            }

            TextButton(onClick = {
                viewModel.resetPassword(context, email) { success, message ->
                    Toast.makeText(
                        context,
                        if (success)
                            context.getString(R.string.password_reset_email_sent)
                        else
                            message ?: context.getString(R.string.email_cannot_be_empty),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
                Text(stringResource(R.string.forgot_password))
            }

            TextButton(onClick = onNavigateToRegister) {
                Text(stringResource(R.string.don_t_have_an_account_register))
            }
        }
    }
}