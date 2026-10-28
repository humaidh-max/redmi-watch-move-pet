package com.movepet.companion

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import java.util.UUID

class MainActivity : ComponentActivity() {
    private lateinit var status: TextView
    private var scanner: BluetoothLeScanner? = null
    private var gatt: BluetoothGatt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(32,48,32,32) }
        status = TextView(this).apply { text = "Move Pet Companion · disconnected"; textSize = 18f }
        val scan = Button(this).apply { text = "Scan for Watch Move"; setOnClickListener { startScan() } }
        layout.addView(status); layout.addView(scan); setContentView(layout)
        requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT), 40)
    }

    private fun startScan() {
        val manager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        scanner = manager.adapter.bluetoothLeScanner
        status.text = "Scanning… keep the watch awake and nearby"
        scanner?.startScan(object : ScanCallback() {
            override fun onScanResult(type: Int, result: ScanResult) {
                val name = result.device.name ?: return
                if (name.contains("Redmi", true) || name.contains("Watch", true)) {
                    scanner?.stopScan(this); connect(result.device)
                }
            }
            override fun onScanFailed(errorCode: Int) { status.text = "Scan failed: $errorCode" }
        })
    }

    private fun connect(device: BluetoothDevice) {
        status.text = "Connecting to ${device.name ?: "watch"}…"
        gatt = device.connectGatt(this, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(g: BluetoothGatt, code: Int, state: Int) {
                if (state == BluetoothProfile.STATE_CONNECTED) { status.text = "Connected · discovering services"; g.discoverServices() }
                else if (state == BluetoothProfile.STATE_DISCONNECTED) status.text = "Disconnected"
            }
            override fun onServicesDiscovered(g: BluetoothGatt, code: Int) {
                val known = WatchProtocol.SERVICE_UUID?.let { g.getService(it) }
                status.text = if (known != null) "GATT ready · tablet bridge online" else "Connected, but Redmi GATT UUIDs are not configured"
            }
        })
    }

    override fun onDestroy() { gatt?.close(); super.onDestroy() }
}
