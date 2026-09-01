# SMS Forwarder

A small native Android app that automatically forwards the text of SMS messages received from one configured phone number to another configured phone number.

## What it does

- Lets you configure a source phone number.
- Lets you configure a destination phone number.
- Lets you enable or disable automatic forwarding.
- Requests `RECEIVE_SMS` and `SEND_SMS` permissions at runtime.
- Handles multipart/long incoming SMS messages as a single message.
- Uses Android phone-number comparison to tolerate common formatting differences.
- Saves a simple last-forward status.

## Build with Codemagic

1. Create a Git repository and upload the complete contents of this folder.
2. In Codemagic, choose **Add application** and connect the repository.
3. Codemagic should detect `codemagic.yaml` in the repository root. The workflow downloads Gradle 8.10.2 on the Codemagic build machine, so no local Gradle or Android Studio installation is required.
4. Select the workflow **SMS Forwarder - Android Debug APK**.
5. Start the build.
6. After a successful build, download the artifact:
   `app/build/outputs/apk/debug/app-debug.apk`
7. Copy/install the APK on your Android phone. Android may ask you to allow installation from the browser/file-manager used to open the APK.

## First run

1. Open the app.
2. Enter the number whose incoming SMS messages should be forwarded.
3. Enter the destination number.
4. Turn on **Enable automatic forwarding**.
5. Tap **Save settings**.
6. Tap **Grant SMS permissions** and allow both SMS permissions.
7. Send a test SMS to the phone from the configured source number.

Using international format such as `+98912...` is recommended.

## Important notes

- The forwarding phone sends a new SMS, so normal carrier SMS charges may apply.
- On dual-SIM phones, Android typically uses the system/default SMS subscription. Set the preferred SMS SIM in Android settings if needed.
- This app forwards SMS text only. MMS/RCS messages are not handled.
- Google Play places policy restrictions on apps requesting SMS permissions. This project is intended for private/sideloaded use.
- Some phone manufacturers have aggressive battery/security controls. Manifest-declared SMS broadcasts normally work even when the app is not open, but vendor-specific security settings can still interfere.

## Package

`com.arian.smsforwarder`
