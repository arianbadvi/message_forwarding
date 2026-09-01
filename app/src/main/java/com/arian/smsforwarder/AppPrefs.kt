package com.arian.smsforwarder

import android.content.Context

object AppPrefs {
    private const val PREFS = "sms_forwarder_prefs"
    private const val KEY_SOURCE = "source_number"
    private const val KEY_DESTINATION = "destination_number"
    private const val KEY_ENABLED = "forwarding_enabled"
    private const val KEY_LAST_STATUS = "last_status"

    fun source(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_SOURCE, "") ?: ""

    fun destination(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_DESTINATION, "") ?: ""

    fun enabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, false)

    fun saveSettings(context: Context, source: String, destination: String, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SOURCE, source.trim())
            .putString(KEY_DESTINATION, destination.trim())
            .putBoolean(KEY_ENABLED, enabled)
            .apply()
    }

    fun setLastStatus(context: Context, status: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LAST_STATUS, status)
            .apply()
    }

    fun lastStatus(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LAST_STATUS, "No message has been forwarded yet.")
            ?: "No message has been forwarded yet."
}
