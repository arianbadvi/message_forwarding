package com.arian.smsforwarder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var sourceEditText: EditText
    private lateinit var destinationEditText: EditText
    private lateinit var enabledSwitch: Switch
    private lateinit var permissionStatus: TextView
    private lateinit var lastStatus: TextView

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val receiveGranted = permissions[Manifest.permission.RECEIVE_SMS] == true ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
            val sendGranted = permissions[Manifest.permission.SEND_SMS] == true ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED

            if (receiveGranted && sendGranted) {
                Toast.makeText(this, "SMS permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Both Receive SMS and Send SMS permissions are required.",
                    Toast.LENGTH_LONG
                ).show()
            }
            updatePermissionStatus()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sourceEditText = findViewById(R.id.sourceNumber)
        destinationEditText = findViewById(R.id.destinationNumber)
        enabledSwitch = findViewById(R.id.enabledSwitch)
        permissionStatus = findViewById(R.id.permissionStatus)
        lastStatus = findViewById(R.id.lastStatus)

        sourceEditText.setText(AppPrefs.source(this))
        destinationEditText.setText(AppPrefs.destination(this))
        enabledSwitch.isChecked = AppPrefs.enabled(this)

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            saveSettings()
        }

        findViewById<Button>(R.id.permissionButton).setOnClickListener {
            requestSmsPermissions()
        }

        updatePermissionStatus()
        lastStatus.text = AppPrefs.lastStatus(this)
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStatus()
        lastStatus.text = AppPrefs.lastStatus(this)
    }

    private fun saveSettings() {
        val source = sourceEditText.text.toString().trim()
        val destination = destinationEditText.text.toString().trim()
        val enabled = enabledSwitch.isChecked

        if (source.isBlank()) {
            sourceEditText.error = "Enter the number whose messages should be forwarded"
            return
        }

        if (destination.isBlank()) {
            destinationEditText.error = "Enter the destination number"
            return
        }

        if (source == destination) {
            destinationEditText.error = "Source and destination should be different numbers"
            return
        }

        AppPrefs.saveSettings(this, source, destination, enabled)
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()

        if (enabled && !hasSmsPermissions()) {
            requestSmsPermissions()
        }
    }

    private fun requestSmsPermissions() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS
            )
        )
    }

    private fun hasSmsPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun updatePermissionStatus() {
        permissionStatus.text = if (hasSmsPermissions()) {
            "SMS permissions: granted"
        } else {
            "SMS permissions: not granted"
        }
    }
}
