package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
  user: UserProfile,
  language: AppLanguage,
  isDarkTheme: Boolean,
  onToggleLanguage: () -> Unit,
  onToggleTheme: () -> Unit,
  onUpgradeClick: () -> Unit,
  onNavigateToMockTests: () -> Unit,
  onNavigateToResults: () -> Unit,
  onNavigateToStudyNotes: () -> Unit,
  onOpenPaymentHistory: () -> Unit,
  onLogout: () -> Unit,
  onOpenAdminAuth: () -> Unit,
  onEditProfile: (name: String, phone: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var showEditProfileDialog by remember { mutableStateOf(false) }
  var showHelpDialog by remember { mutableStateOf(false) }
  var showPrivacyDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("profile_screen"),
    contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp)
  ) {
    // 1. Student Profile Information Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(SoftBlueSecondary),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = user.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
              color = DarkNavyText,
              fontSize = 24.sp,
              fontWeight = FontWeight.ExtraBold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = user.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = DarkNavyText
          )

          Text(
            text = user.email,
            fontSize = 12.sp,
            color = SlateGreyText
          )

          if (user.phone.isNotBlank()) {
            Text(
              text = user.phone,
              fontSize = 11.sp,
              color = SlateGreyText
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              color = SoftBlueContainer,
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
            ) {
              Text(
                text = "Target: ${user.targetExamName}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkNavyText,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }

            OutlinedButton(
              onClick = { showEditProfileDialog = true },
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              modifier = Modifier.height(30.dp)
            ) {
              Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp), tint = SkyBluePrimary)
              Spacer(modifier = Modifier.width(4.dp))
              Text("Edit", fontSize = 11.sp, color = SkyBluePrimary, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 2. Premium Membership Status Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (user.isPremium) WbGoldContainer.copy(alpha = 0.6f) else SoftBlueContainer
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (user.isPremium) WbGold else BorderSoftBlue
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Icon(
              imageVector = if (user.isPremium) Icons.Default.WorkspacePremium else Icons.Outlined.StarOutline,
              contentDescription = null,
              tint = if (user.isPremium) Color(0xFFB78103) else SkyBluePrimary,
              modifier = Modifier.size(28.dp)
            )
            Column {
              Text(
                text = if (user.isPremium) "WBTOPPER PRO Active" else "Premium Membership",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (user.isPremium) Color(0xFFB78103) else DarkNavyText
              )
              Text(
                text = if (user.isPremium) "Valid until ${user.premiumExpiryDate ?: "Active"}" else "Full access to all 15 exam test series @ ₹199",
                fontSize = 11.sp,
                color = SlateGreyText
              )
            }
          }

          if (!user.isPremium) {
            Button(
              onClick = onUpgradeClick,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text("₹199", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
            }
          }
        }
      }
    }

    // 3. Student Navigation & Features Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Text(
          text = if (language == AppLanguage.BENGALI) "আমার শেখার ড্যাশবোর্ড" else "My Learning Activities",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = DarkNavyText
        )
        Spacer(modifier = Modifier.height(6.dp))

        ProfileMenuOption(
          icon = Icons.Outlined.Assignment,
          title = "My Mock Tests",
          subtitle = "View test series, attempts and pending papers",
          onClick = onNavigateToMockTests
        )

        ProfileMenuOption(
          icon = Icons.Outlined.Analytics,
          title = "My Results",
          subtitle = "Performance analytics, rank and solutions",
          onClick = onNavigateToResults
        )

        ProfileMenuOption(
          icon = Icons.Outlined.MenuBook,
          title = "My Study Notes",
          subtitle = "Saved PDF chapters and clinical cheat-sheets",
          onClick = onNavigateToStudyNotes
        )

        ProfileMenuOption(
          icon = Icons.Outlined.ReceiptLong,
          title = "Payment History",
          subtitle = "Verified Razorpay receipts and plan status",
          onClick = onOpenPaymentHistory
        )
      }
    }

    // 4. App Settings Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp)
      ) {
        Text(
          text = if (language == AppLanguage.BENGALI) "অ্যাপ সেটিংস" else "Preferences & Support",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = DarkNavyText
        )
        Spacer(modifier = Modifier.height(6.dp))

        ProfileMenuOption(
          icon = Icons.Outlined.Translate,
          title = if (language == AppLanguage.BENGALI) "ভাষা (Language)" else "App Language",
          subtitle = if (language == AppLanguage.BENGALI) "বাংলা (Bengali)" else "English",
          onClick = onToggleLanguage
        )

        ProfileMenuOption(
          icon = Icons.Outlined.DarkMode,
          title = "Theme Mode",
          subtitle = if (isDarkTheme) "Dark Theme" else "Light Sky Blue",
          onClick = onToggleTheme
        )

        ProfileMenuOption(
          icon = Icons.Outlined.HelpOutline,
          title = "Help & Support",
          subtitle = "FAQs, syllabus help and student queries",
          onClick = { showHelpDialog = true }
        )

        ProfileMenuOption(
          icon = Icons.Outlined.Policy,
          title = "Privacy Policy & Terms",
          subtitle = "Student data protection guidelines",
          onClick = { showPrivacyDialog = true }
        )

        ProfileMenuOption(
          icon = Icons.Outlined.Logout,
          title = "Logout",
          subtitle = "Sign out from this device",
          onClick = onLogout,
          iconTint = AlertRed,
          titleColor = AlertRed
        )
      }
    }

    // 5. SEPARATED DISCREET ADMIN LOGIN AT THE ABSOLUTE BOTTOM (SECTION 6 REQUIREMENT)
    item {
      Spacer(modifier = Modifier.height(30.dp))
      HorizontalDivider(
        modifier = Modifier.padding(horizontal = 32.dp),
        color = BorderSoftBlue.copy(alpha = 0.8f)
      )
      Spacer(modifier = Modifier.height(14.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Discreet, small text, subtle grey/light-blue styling with small shield icon
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onOpenAdminAuth() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("discreet_admin_login_button"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "Admin Login",
            tint = SlateGreyText,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "Admin Login",
            fontSize = 11.sp,
            color = SlateGreyText,
            fontWeight = FontWeight.Medium
          )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "WBTOPPER • v1.2.0 • Secured by Firebase",
          fontSize = 10.sp,
          color = SlateGreyText.copy(alpha = 0.7f)
        )
      }
    }
  }

  // Edit Profile Dialog
  if (showEditProfileDialog) {
    var editName by remember { mutableStateOf(user.name) }
    var editPhone by remember { mutableStateOf(user.phone) }

    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = { Text("Edit Student Profile", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = editName,
            onValueChange = { editName = it },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = editPhone,
            onValueChange = { editPhone = it },
            label = { Text("Mobile Number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onEditProfile(editName, editPhone)
            showEditProfileDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
        ) {
          Text("Save Changes")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel") }
      }
    )
  }

  // Help & Support Dialog
  if (showHelpDialog) {
    AlertDialog(
      onDismissRequest = { showHelpDialog = false },
      title = { Text("WBTOPPER Student Support", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text("For questions regarding JENPAS-UG, ANM & GNM, WB CHO, or mock test results:")
          Text("• Email: support@wbtopper.com", fontWeight = FontWeight.Bold, color = SkyBluePrimary)
          Text("• Helpline: +91 98301 23456 (10 AM - 6 PM IST)")
          Text("• Technical Portal: https://wbtopper.com")
        }
      },
      confirmButton = {
        Button(onClick = { showHelpDialog = false }) { Text("Close") }
      }
    )
  }

  // Privacy Policy Dialog
  if (showPrivacyDialog) {
    AlertDialog(
      onDismissRequest = { showPrivacyDialog = false },
      title = { Text("Privacy Policy & Terms", fontWeight = FontWeight.Bold) },
      text = {
        Text(
          "WBTOPPER protects all candidate test attempts, question bookmarks, and payment records under 256-bit encryption. Student scores and attempt histories are private and never shared without authorization.",
          fontSize = 12.sp,
          lineHeight = 17.sp
        )
      },
      confirmButton = {
        Button(onClick = { showPrivacyDialog = false }) { Text("Understood") }
      }
    )
  }
}

@Composable
private fun ProfileMenuOption(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  iconTint: Color = SlateGreyText,
  titleColor: Color = DarkNavyText
) {
  Surface(
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    shape = RoundedCornerShape(12.dp),
    color = CardWhite,
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(SoftBlueContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (iconTint == SlateGreyText) SkyBluePrimary else iconTint,
          modifier = Modifier.size(18.dp)
        )
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = titleColor)
        Text(text = subtitle, fontSize = 11.sp, color = SlateGreyText)
      }

      Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SlateGreyText.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
    }
  }
}
