package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.ExamAlert
import com.example.model.ExamCategory
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun WbTopBar(
  user: UserProfile,
  language: AppLanguage,
  isDarkTheme: Boolean,
  onToggleLanguage: () -> Unit,
  onToggleTheme: () -> Unit,
  onProfileClick: () -> Unit,
  onNotificationClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp,
    shadowElevation = 1.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: Logo & Brand Name
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // App Logo Icon Badge (Light Sky Blue gradient/accent)
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SkyBluePrimary)
            .padding(6.dp),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.School,
            contentDescription = "WBTOPPER Logo",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
          )
        }

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "WBTOPPER",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
              ),
              color = DarkNavyText
            )
            if (user.isPremium) {
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = WbGoldContainer,
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, WbGold)
              ) {
                Text(
                  text = "PRO",
                  color = Color(0xFFB78103),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
          }
          Text(
            text = if (language == AppLanguage.BENGALI) "আপনার পরীক্ষার প্রস্তুতি সঙ্গী" else "Your Exam Preparation Partner",
            style = MaterialTheme.typography.labelSmall,
            color = SlateGreyText
          )
        }
      }

      // Right: Actions (Language toggle, Dark/Light mode, Notification bell, User profile avatar)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Bengali / English Language Toggle
        FilledTonalButton(
          onClick = onToggleLanguage,
          modifier = Modifier
            .height(34.dp)
            .testTag("language_toggle_button"),
          contentPadding = PaddingValues(horizontal = 8.dp),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = SoftBlueSecondary.copy(alpha = 0.5f)
          )
        ) {
          Text(
            text = if (language == AppLanguage.ENGLISH) "বাংলা" else "ENG",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = SkyBluePrimary
          )
        }

        // Dark/Light Mode Toggle
        IconButton(
          onClick = onToggleTheme,
          modifier = Modifier
            .size(36.dp)
            .testTag("theme_toggle_button")
        ) {
          Icon(
            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = "Toggle theme",
            tint = SlateGreyText,
            modifier = Modifier.size(20.dp)
          )
        }

        // Notification Bell Icon
        IconButton(
          onClick = onNotificationClick,
          modifier = Modifier
            .size(36.dp)
            .testTag("top_notification_button")
        ) {
          Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = "Notifications",
            tint = SlateGreyText,
            modifier = Modifier.size(22.dp)
          )
        }

        // User Profile Icon (Tapping navigates to profile tab)
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(SoftBlueSecondary)
            .clickable { onProfileClick() }
            .testTag("top_profile_avatar"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile",
            tint = DarkNavyText,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
fun BreakingTicker(
  alerts: List<ExamAlert>,
  language: AppLanguage,
  onClick: (ExamAlert) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val activeAlert = alerts.firstOrNull { it.isBreaking } ?: alerts.firstOrNull() ?: return

  Surface(
    onClick = { onClick(activeAlert) },
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    shape = RoundedCornerShape(10.dp),
    color = CardWhite,
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue),
    shadowElevation = 0.5.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Red BREAKING badge
      Surface(
        color = AlertRed,
        shape = RoundedCornerShape(6.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(12.dp)
          )
          Text(
            text = "BREAKING",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
          )
        }
      }

      // Ticker text
      Text(
        text = if (language == AppLanguage.BENGALI) activeAlert.titleBn else activeAlert.title,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(1f),
        color = DarkNavyText
      )

      Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = null,
        tint = SlateGreyText,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

@Composable
fun FeaturedAlertCard(
  alert: ExamAlert,
  language: AppLanguage,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    onClick = onClick,
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
      .testTag("featured_alert_card"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = CardWhite
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSoftBlue)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Notification Bell Icon with soft red container
      Box(
        modifier = Modifier
          .size(46.dp)
          .clip(CircleShape)
          .background(Color(0xFFFEE2E2)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.NotificationsActive,
          contentDescription = "Alert Bell",
          tint = AlertRed,
          modifier = Modifier.size(24.dp)
        )
      }

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            color = Color(0xFFFEE2E2),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = if (language == AppLanguage.BENGALI) "জরুরি বিজ্ঞপ্তি" else "IMPORTANT ANNOUNCEMENT",
              color = AlertRed,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
          }

          Text(
            text = alert.date,
            color = SlateGreyText,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = if (language == AppLanguage.BENGALI) alert.titleBn else alert.title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            lineHeight = 19.sp
          ),
          color = DarkNavyText,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = alert.description,
          style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 11.sp,
            lineHeight = 15.sp
          ),
          color = SlateGreyText,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = if (language == AppLanguage.BENGALI) "সব সতর্কতা দেখুন >" else "View All Alerts >",
            color = SkyBluePrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun ExamCategoryCard(
  category: ExamCategory,
  language: AppLanguage,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    onClick = onClick,
    modifier = modifier
      .fillMaxWidth()
      .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
      .testTag("exam_category_card_${category.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = CardWhite
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = BorderSoftBlue
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        // Pastel Icon container
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(category.bgHex)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = WbIcons.getCategoryIcon(category.iconKey),
            contentDescription = category.title,
            tint = Color(category.iconTintHex),
            modifier = Modifier.size(24.dp)
          )
        }

        // Optional badge ("Admit Card Out", "Syllabus Live", "Exam Date Soon")
        if (category.badgeText != null) {
          val isAlert = category.badgeText.contains("Admit", ignoreCase = true) ||
              category.badgeText.contains("Merit", ignoreCase = true)

          Surface(
            color = if (isAlert) Color(0xFFFEE2E2) else Color(0xFFE0F2FE),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = category.badgeText,
              color = if (isAlert) AlertRed else SkyBluePrimary,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Exam Title
      Text(
        text = if (language == AppLanguage.BENGALI) category.bengaliTitle else category.title,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          lineHeight = 18.sp
        ),
        color = DarkNavyText,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(3.dp))

      // Subtitle / Description
      Text(
        text = if (language == AppLanguage.BENGALI) category.bengaliSubtitle else category.subtitle,
        style = MaterialTheme.typography.bodySmall.copy(
          fontSize = 11.sp,
          lineHeight = 14.sp
        ),
        color = SlateGreyText,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Arrow or chevron action indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(SuccessGreen)
          )
          Text(
            text = if (language == AppLanguage.BENGALI) "সক্রিয়" else "Active",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = SlateGreyText
          )
        }

        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(SoftBlueSecondary.copy(alpha = 0.4f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Open",
            tint = SkyBluePrimary,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
