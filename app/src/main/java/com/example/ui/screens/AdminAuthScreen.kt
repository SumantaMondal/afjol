package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAuthScreen(
  isLoading: Boolean,
  adminIdentifierDefault: String = "admin@wbtopper.com",
  onAuthenticateAdmin: (adminId: String, pass: String) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var adminId by remember { mutableStateOf(adminIdentifierDefault) }
  var adminPassword by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var authError by remember { mutableStateOf<String?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Administrator Access",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = DarkNavyText
          )
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkNavyText)
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
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(DarkNavyText),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = "Admin Shield",
          tint = CardWhite,
          modifier = Modifier.size(36.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "WBTOPPER Portal Management",
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp,
        color = DarkNavyText
      )

      Text(
        text = "Authorized Personnel & Administrative Sign-in",
        fontSize = 12.sp,
        color = SlateGreyText
      )

      Spacer(modifier = Modifier.height(24.dp))

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
          if (authError != null) {
            Surface(
              color = Color(0xFFFEE2E2),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = AlertRed, modifier = Modifier.size(16.dp))
                Text(authError ?: "", color = AlertRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
              }
            }
          }

          OutlinedTextField(
            value = adminId,
            onValueChange = { adminId = it },
            label = { Text("Admin Identifier") },
            leadingIcon = { Icon(Icons.Outlined.AdminPanelSettings, contentDescription = null, tint = SlateGreyText) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )

          OutlinedTextField(
            value = adminPassword,
            onValueChange = { adminPassword = it },
            label = { Text("Security Key / Password") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = SlateGreyText) },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = "Toggle password"
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(6.dp))

          Button(
            onClick = {
              if (adminId.isBlank() || adminPassword.isBlank()) {
                authError = "Please enter administrator credentials."
                return@Button
              }
              authError = null
              onAuthenticateAdmin(adminId, adminPassword)
            },
            enabled = !isLoading,
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("admin_authenticate_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkNavyText)
          ) {
            if (isLoading) {
              CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
              Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Verify & Access Dashboard", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "Access is protected by Firebase Authentication and server-verified custom claims. All administrative operations are monitored and logged.",
        fontSize = 11.sp,
        color = SlateGreyText,
        modifier = Modifier.padding(horizontal = 16.dp),
        lineHeight = 16.sp
      )
    }
  }
}
