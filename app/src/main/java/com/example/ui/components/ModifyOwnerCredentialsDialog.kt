package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.AnimeViewModel
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Ultra-secure Dialog allowing ONLY the verified Owner to modify
 * Owner Dashboard Username, Admin Email/UID, and Master Password/PIN.
 */
@Composable
fun ModifyOwnerCredentialsDialog(
    viewModel: AnimeViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var currentPinInput by remember { mutableStateOf("") }
    var newUsernameInput by remember { mutableStateOf(viewModel.getOwnerUsername()) }
    var newEmailInput by remember { mutableStateOf(viewModel.getOwnerEmail()) }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }

    var isCurrentPinVisible by remember { mutableStateOf(false) }
    var isNewPinVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false, usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("modify_owner_credentials_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AnimeGold, AnimePurple, AnimePink)))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "👑 Edit Owner Credentials",
                                color = AnimeGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Strictly Owner-Only Access",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Security Notice Banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = AnimePurple.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🔒 To prevent unauthorized alterations, enter your current Master PIN/Password to confirm identity before saving changes.",
                        color = AnimeCyan,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Current Master PIN (Required)
                Text("Current Master PIN / Password:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = currentPinInput,
                    onValueChange = {
                        currentPinInput = it
                        errorMessage = null
                    },
                    placeholder = { Text("Enter current PIN (e.g. 1234)", color = TextMuted, fontSize = 12.sp) },
                    visualTransformation = if (isCurrentPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isCurrentPinVisible = !isCurrentPinVisible }) {
                            Icon(
                                if (isCurrentPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AnimeGold) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().testTag("current_pin_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeGold,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. New Owner Username
                Text("New Owner Username / Display Name:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = newUsernameInput,
                    onValueChange = {
                        newUsernameInput = it
                        errorMessage = null
                    },
                    placeholder = { Text("e.g. Aman Jangra (Owner)", color = TextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AnimeCyan) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_username_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3. New Owner Email / Admin UID
                Text("New Owner Email (Admin Login ID):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = newEmailInput,
                    onValueChange = {
                        newEmailInput = it
                        errorMessage = null
                    },
                    placeholder = { Text("e.g. amjangra0@gmail.com", color = TextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AnimePink) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().testTag("new_email_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimePink,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 4. New Master Password / PIN
                Text("New Master Password / PIN:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = newPinInput,
                    onValueChange = {
                        newPinInput = it
                        errorMessage = null
                    },
                    placeholder = { Text("Leave blank to keep current PIN", color = TextMuted, fontSize = 12.sp) },
                    visualTransformation = if (isNewPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isNewPinVisible = !isNewPinVisible }) {
                            Icon(
                                if (isNewPinVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = AnimeCyan) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().testTag("new_pin_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                if (newPinInput.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Confirm New Master Password / PIN:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = confirmPinInput,
                        onValueChange = {
                            confirmPinInput = it
                            errorMessage = null
                        },
                        placeholder = { Text("Re-enter new PIN to confirm", color = TextMuted, fontSize = 12.sp) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth().testTag("confirm_pin_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCyan,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Error message banner
                errorMessage?.let { err ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            if (currentPinInput.isBlank()) {
                                errorMessage = "Please enter your current Master PIN."
                                return@Button
                            }
                            if (newUsernameInput.isBlank()) {
                                errorMessage = "Username cannot be empty."
                                return@Button
                            }
                            if (newEmailInput.isBlank() || !newEmailInput.contains("@")) {
                                errorMessage = "Please enter a valid Owner Email."
                                return@Button
                            }
                            val targetPin = if (newPinInput.isNotBlank()) {
                                if (newPinInput != confirmPinInput) {
                                    errorMessage = "New PIN and Confirm PIN do not match!"
                                    return@Button
                                }
                                if (newPinInput.length < 4) {
                                    errorMessage = "New PIN must be at least 4 characters long."
                                    return@Button
                                }
                                newPinInput
                            } else {
                                currentPinInput
                            }

                            val result = viewModel.updateOwnerCredentials(
                                newUsername = newUsernameInput,
                                newEmail = newEmailInput,
                                newPasswordOrPin = targetPin,
                                currentPin = currentPinInput
                            )

                            if (result.isSuccess) {
                                Toast.makeText(context, "👑 Owner Credentials successfully updated!", Toast.LENGTH_LONG).show()
                                onSuccess()
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Failed to update owner credentials."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.5f).testTag("save_owner_credentials_btn")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.surface, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Update Credentials", color = MaterialTheme.colorScheme.surface, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
