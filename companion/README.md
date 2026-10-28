# Move Pet Android Companion

This is the native Android bridge needed for real Bluetooth work. It scans for a Redmi/Watch device, connects with Android GATT, and provides the place to relay watch data to the tablet hub.

## Run

1. Open `companion/` in Android Studio Ladybug or newer.
2. Let Gradle sync.
3. Connect an Android tablet/phone with USB debugging enabled.
4. Install and launch **Move Pet Companion**.
5. Grant Bluetooth permissions.
6. Keep the watch awake and tap **Scan for Watch Move**.

## Important limitation

The app intentionally leaves the Redmi-specific GATT UUIDs as `null` in `WatchProtocol.kt`. Xiaomi’s proprietary service and characteristic UUIDs are required before voice packets or telemetry can be read/written. Without those values, the app can scan, connect, discover services, and report the missing protocol, but it cannot control the watch.

The existing web dashboard remains usable through its simulated BLE bridge until the protocol is known.
