package com.arian.smsforwarder

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Telephony
import android.telephony.PhoneNumberUtils
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        if (!AppPrefs.enabled(context)) return

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            AppPrefs.setLastStatus(context, "Forwarding failed: Send SMS permission is not granted.")
            return
        }

        val configuredSource = AppPrefs.source(context)
        val destination = AppPrefs.destination(context)
        if (configuredSource.isBlank() || destination.isBlank()) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isEmpty()) return

        // Android may deliver one long SMS as multiple PDU parts. Joining the bodies
        // here prevents the app from forwarding each part as a separate incoming SMS.
        val sender = messages.firstOrNull()?.displayOriginatingAddress
            ?: messages.firstOrNull()?.originatingAddress
            ?: return
        val body = messages.joinToString(separator = "") { it.displayMessageBody ?: it.messageBody ?: "" }

        if (!numbersMatch(sender, configuredSource)) return

        try {
            val smsManager = context.getSystemService(SmsManager::class.java)
            val parts = smsManager.divideMessage(body)

            if (parts.size <= 1) {
                smsManager.sendTextMessage(destination, null, body, null, null)
            } else {
                smsManager.sendMultipartTextMessage(destination, null, parts, null, null)
            }

            val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            AppPrefs.setLastStatus(
                context,
                "Forward requested at $time\nFrom: $sender\nTo: $destination"
            )
        } catch (e: Exception) {
            AppPrefs.setLastStatus(
                context,
                "Forwarding failed: ${e.message ?: e.javaClass.simpleName}"
            )
        }
    }

    private fun numbersMatch(received: String, configured: String): Boolean {
        if (PhoneNumberUtils.compare(received, configured)) return true

        // Fallback for common formatting differences such as spaces, hyphens and '+'.
        val a = PhoneNumberUtils.normalizeNumber(received)
        val b = PhoneNumberUtils.normalizeNumber(configured)
        return a.isNotBlank() && a == b
    }
}
