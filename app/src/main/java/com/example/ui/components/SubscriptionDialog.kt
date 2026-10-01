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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
import com.example.ui.theme.*

@Composable
fun SubscriptionDialog(
  reason: String,
  language: AppLanguage,
  isProcessing: Boolean,
  onDismiss: () -> Unit,
  onPaySuccess: (String) -> Unit
) {
  var selectedMethod by remember { mutableStateOf("UPI (Google Pay / PhonePe)") }

  Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("subscription_dialog"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(
        containerColor = CardWhite
      )
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Razorpay secure header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFF0C2340)
            ) {
              Text(
                text = "Razorpay",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(
              text = "Secured Checkout",
              fontSize = 11.sp,
              color = SlateGreyText
            )
          }

          IconButton(
            onClick = onDismiss,
            enabled = !isProcessing,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Crown / Trophy Icon
        Box(
          modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(WbGoldContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.WorkspacePremium,
            contentDescription = "Premium Badge",
            tint = WbGold,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = if (language == AppLanguage.BENGALI) "WBTOPPER প্রিমিয়াম পাস" else "WBTOPPER All-Access Premium",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = DarkNavyText
        )

        Text(
          text = reason.ifBlank { "Unlock unlimited mock tests, PDF notes & clinical PYQs" },
          style = MaterialTheme.typography.bodySmall,
          color = SlateGreyText,
          modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Price Badge Card
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          color = SoftBlueContainer,
          border = androidx.compose.foundation.BorderStroke(1.dp, SkyBluePrimary.copy(alpha = 0.3f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "1-Year Unlimited Access",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = DarkNavyText
              )
              Text(
                text = "All 15 Medical & Nursing Exams",
                fontSize = 11.sp,
                color = SlateGreyText
              )
            }
            Row(verticalAlignment = Alignment.Bottom) {
              Text(
                text = "₹999",
                fontSize = 13.sp,
                color = SlateGreyText,
                style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough),
                modifier = Modifier.padding(bottom = 2.dp, end = 4.dp)
              )
              Text(
                text = "₹199",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SkyBluePrimary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Features Checklist
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          PremiumBenefitRow("Unlimited CBT Mock Tests with Instant Ranking")
          PremiumBenefitRow("Full Digital PDF Study Library & Handbooks")
          PremiumBenefitRow("Subject-wise Detailed Explanations in Bengali & English")
          PremiumBenefitRow("Previous 10 Years Solved Question Papers (PYQs)")
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Payment Method Selector
        Text(
          text = "Select Payment Method",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = DarkNavyText,
          modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(6.dp))

        val methods = listOf(
          "UPI (Google Pay / PhonePe / Paytm)",
          "Debit / Credit Card (Visa, RuPay, MC)",
          "Net Banking (SBI, HDFC, PNB, etc.)"
        )

        methods.forEach { method ->
          val isSelected = selectedMethod == method
          Surface(
            onClick = { selectedMethod = method },
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp),
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) SoftBlueSecondary.copy(alpha = 0.4f) else CardWhite,
            border = androidx.compose.foundation.BorderStroke(
              width = if (isSelected) 1.5.dp else 1.dp,
              color = if (isSelected) SkyBluePrimary else BorderSoftBlue
            )
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = method,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = DarkNavyText
              )
              RadioButton(
                selected = isSelected,
                onClick = { selectedMethod = method },
                colors = RadioButtonDefaults.colors(selectedColor = SkyBluePrimary)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pay CTA Button
        Button(
          onClick = { onPaySuccess(selectedMethod) },
          enabled = !isProcessing,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("razorpay_pay_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
        ) {
          if (isProcessing) {
            CircularProgressIndicator(
              modifier = Modifier.size(20.dp),
              color = Color.White,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("Verifying Razorpay Order...", color = Color.White, fontWeight = FontWeight.Bold)
          } else {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (language == AppLanguage.BENGALI) "এখনই ₹১৯৯ পরিশোধ করুন" else "Pay ₹199 Securely",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "256-Bit SSL Encryption • Instant Activation",
          fontSize = 10.sp,
          color = SlateGreyText
        )
      }
    }
  }
}

@Composable
private fun PremiumBenefitRow(text: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Icon(
      imageVector = Icons.Default.CheckCircle,
      contentDescription = null,
      tint = SuccessGreen,
      modifier = Modifier.size(16.dp)
    )
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall,
      color = DarkNavyText
    )
  }
}
