package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.ExamCategory
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
  categories: List<ExamCategory>,
  language: AppLanguage,
  isLoading: Boolean,
  onLogin: (email: String, pass: String) -> Unit,
  onSignup: (name: String, email: String, pass: String, phone: String, examId: String) -> Unit,
  onGoogleSignIn: () -> Unit,
  onForgotPassword: (email: String) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isLoginMode by remember { mutableStateOf(true) }

  // Form Fields
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var fullName by remember { mutableStateOf("") }
  var phoneNumber by remember { mutableStateOf("") }
  var selectedExamId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "jenpas_ug") }
  var termsAccepted by remember { mutableStateOf(true) }

  var passwordVisible by remember { mutableStateOf(false) }
  var confirmPasswordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var showForgotDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {},
        navigationIcon = {
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = DarkNavyText)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundBlueWhite)
      )
    },
    containerColor = BackgroundBlueWhite,
    modifier = modifier.fillMaxSize()
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // WBTOPPER Logo
      Box(
        modifier = Modifier
          .size(56.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(SkyBluePrimary),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.School,
          contentDescription = "Logo",
          tint = Color.White,
          modifier = Modifier.size(34.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "WBTOPPER",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 0.5.sp
        ),
        color = DarkNavyText
      )

      Text(
        text = if (language == AppLanguage.BENGALI)
          "আপনার পরীক্ষার প্রস্তুতি সঙ্গী"
        else
          "Your Exam Preparation Partner",
        style = MaterialTheme.typography.bodySmall,
        color = SlateGreyText
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Tab switcher: Login / Create Account
      Surface(
        color = SoftBlueSecondary.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (isLoginMode) CardWhite else Color.Transparent)
              .clickable {
                isLoginMode = true
                errorMessage = null
              }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (language == AppLanguage.BENGALI) "লগইন" else "Login",
              fontWeight = if (isLoginMode) FontWeight.Bold else FontWeight.Medium,
              color = if (isLoginMode) SkyBluePrimary else SlateGreyText,
              fontSize = 13.sp
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .background(if (!isLoginMode) CardWhite else Color.Transparent)
              .clickable {
                isLoginMode = false
                errorMessage = null
              }
              .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = if (language == AppLanguage.BENGALI) "নতুন একাউন্ট" else "Sign Up",
              fontWeight = if (!isLoginMode) FontWeight.Bold else FontWeight.Medium,
              color = if (!isLoginMode) SkyBluePrimary else SlateGreyText,
              fontSize = 13.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Error message banner
      if (errorMessage != null) {
        Surface(
          color = Color(0xFFFEE2E2),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = AlertRed, modifier = Modifier.size(16.dp))
            Text(errorMessage ?: "", color = AlertRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
          }
        }
      }

      // Fields
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          if (!isLoginMode) {
            // Full Name (Signup only)
            OutlinedTextField(
              value = fullName,
              onValueChange = { fullName = it },
              label = { Text("Full Name") },
              leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = SlateGreyText) },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              shape = RoundedCornerShape(12.dp)
            )
          }

          // Email field
          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = SlateGreyText) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp)
          )

          if (!isLoginMode) {
            // Phone Number (Signup only)
            OutlinedTextField(
              value = phoneNumber,
              onValueChange = { phoneNumber = it },
              label = { Text("Mobile Number (Optional)") },
              leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = SlateGreyText) },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              shape = RoundedCornerShape(12.dp)
            )

            // Target Exam Selector Dropdown (Signup only)
            var expandedExamDropdown by remember { mutableStateOf(false) }
            val currentSelectedCategory = categories.find { it.id == selectedExamId } ?: categories.firstOrNull()

            ExposedDropdownMenuBox(
              expanded = expandedExamDropdown,
              onExpandedChange = { expandedExamDropdown = it }
            ) {
              OutlinedTextField(
                value = currentSelectedCategory?.title ?: "Select Target Exam",
                onValueChange = {},
                readOnly = true,
                label = { Text("Target Exam") },
                leadingIcon = { Icon(Icons.Outlined.School, contentDescription = null, tint = SlateGreyText) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedExamDropdown) },
                modifier = Modifier
                  .menuAnchor()
                  .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
              )

              ExposedDropdownMenu(
                expanded = expandedExamDropdown,
                onDismissRequest = { expandedExamDropdown = false }
              ) {
                categories.forEach { cat ->
                  DropdownMenuItem(
                    text = { Text(cat.title, fontWeight = FontWeight.Medium) },
                    onClick = {
                      selectedExamId = cat.id
                      expandedExamDropdown = false
                    }
                  )
                }
              }
            }
          }

          // Password field
          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = SlateGreyText) },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = "Toggle password visibility"
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )

          if (!isLoginMode) {
            // Confirm Password field
            OutlinedTextField(
              value = confirmPassword,
              onValueChange = { confirmPassword = it },
              label = { Text("Confirm Password") },
              leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = SlateGreyText) },
              trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                  Icon(
                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle confirm password"
                  )
                }
              },
              visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              shape = RoundedCornerShape(12.dp)
            )

            // Terms Checkbox
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Checkbox(
                checked = termsAccepted,
                onCheckedChange = { termsAccepted = it },
                colors = CheckboxDefaults.colors(checkedColor = SkyBluePrimary)
              )
              Text(
                text = "I agree to Terms & Conditions and Privacy Policy",
                fontSize = 11.sp,
                color = SlateGreyText
              )
            }
          } else {
            // Forgot Password Link
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              Text(
                text = "Forgot Password?",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = SkyBluePrimary,
                modifier = Modifier.clickable { showForgotDialog = true }
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Primary Action Button (Login / Create Account)
          Button(
            onClick = {
              errorMessage = null
              if (email.isBlank() || password.isBlank()) {
                errorMessage = "Please enter both email and password."
                return@Button
              }
              if (!isLoginMode) {
                if (fullName.isBlank()) {
                  errorMessage = "Please enter your full name."
                  return@Button
                }
                if (password != confirmPassword) {
                  errorMessage = "Passwords do not match."
                  return@Button
                }
                if (!termsAccepted) {
                  errorMessage = "Please accept the Terms & Conditions."
                  return@Button
                }
                onSignup(fullName, email, password, phoneNumber, selectedExamId)
              } else {
                onLogin(email, password)
              }
            },
            enabled = !isLoading,
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("auth_submit_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
          ) {
            if (isLoading) {
              CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
              Text(
                text = if (isLoginMode) "Log In" else "Create Account",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
              )
            }
          }

          // Or Divider
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = BorderSoftBlue)
            Text(
              text = "  OR  ",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = SlateGreyText
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = BorderSoftBlue)
          }

          // Google Sign-In Button
          OutlinedButton(
            onClick = onGoogleSignIn,
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("google_sign_in_button"),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
          ) {
            Icon(
              imageVector = Icons.Default.AccountCircle,
              contentDescription = "Google",
              tint = SkyBluePrimary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Continue with Google",
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp,
              color = DarkNavyText
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Forgot Password Dialog
  if (showForgotDialog) {
    var resetEmail by remember { mutableStateOf(email) }
    var resetSent by remember { mutableStateOf(false) }

    AlertDialog(
      onDismissRequest = { showForgotDialog = false },
      title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          if (!resetSent) {
            Text("Enter your registered email address to receive password reset instructions.")
            OutlinedTextField(
              value = resetEmail,
              onValueChange = { resetEmail = it },
              label = { Text("Email Address") },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true
            )
          } else {
            Text(
              text = "Password reset link has been dispatched to $resetEmail.",
              color = SuccessGreen,
              fontWeight = FontWeight.Medium
            )
          }
        }
      },
      confirmButton = {
        if (!resetSent) {
          Button(
            onClick = {
              if (resetEmail.isNotBlank()) {
                onForgotPassword(resetEmail)
                resetSent = true
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
          ) {
            Text("Send Reset Link")
          }
        } else {
          Button(onClick = { showForgotDialog = false }) {
            Text("Done")
          }
        }
      },
      dismissButton = {
        if (!resetSent) {
          TextButton(onClick = { showForgotDialog = false }) { Text("Cancel") }
        }
      }
    )
  }
}
