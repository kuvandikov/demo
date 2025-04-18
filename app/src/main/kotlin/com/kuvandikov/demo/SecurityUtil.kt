package com.kuvandikov.demo


import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.BatteryManager
import android.os.Build
import android.view.InputDevice
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Locale

object SecurityUtil {

    val isDeviceRooted: Boolean
        get() = checkRootMethods()

    fun isEmulator(context: Context): Boolean {
        return checkBuildProperties() ||
                !hasRealSensors(context) ||
                !isBatteryPresent(context) ||
                isCpuInfoSuspect() ||
                hasSuspiciousInputDevices()
    }

    private fun checkRootMethods(): Boolean {
        return checkCommonRootPaths() || checkSuBinary()
    }

    fun checkCommonRootPaths(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su",
            "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su",
            "/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"
        )
        return paths.any { File(it).exists() }
    }

    fun checkSuBinary(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            BufferedReader(InputStreamReader(process.inputStream)).readLine() != null
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }

    fun checkBuildProperties(): Boolean {
        val suspectBuildProps = listOf(
            Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"),
            Build.FINGERPRINT.startsWith("generic"),
            Build.FINGERPRINT.startsWith("unknown"),
            Build.HARDWARE.contains("goldfish", true),
            Build.HARDWARE.contains("ranchu", true),
            Build.HARDWARE.equals("vbox86", true),
            Build.HARDWARE.contains("nox", true),
            Build.MODEL.contains("google_sdk", true),
            Build.MODEL.contains("Emulator", true),
            Build.MODEL.contains("Android SDK built for x86", true),
            Build.MODEL.contains("droid4x", true),
            Build.MANUFACTURER.contains("Genymotion", true),
            Build.PRODUCT.contains("sdk_google", true),
            Build.PRODUCT.contains("google_sdk", true),
            Build.PRODUCT.contains("sdk", true),
            Build.PRODUCT.contains("sdk_x86", true),
            Build.PRODUCT.contains("vbox86p", true),
            Build.PRODUCT.contains("emulator", true),
            Build.PRODUCT.contains("simulator", true),
            Build.PRODUCT.contains("nox", true),
            Build.BOARD.contains("nox", true),
            Build.BOOTLOADER.contains("nox", true),
            Build.BOOTLOADER.contains("bluestacks",true),
            Build.BOOTLOADER.contains("genymotion",true),
            Build.BOOTLOADER.contains("vbox",true),
            Build.BOOTLOADER.contains("qemu",true),
            Build.BOOTLOADER.contains("android_x86",true),
            Build.SERIAL.contains("nox", true)
        )
        return suspectBuildProps.any { it }
    }

    fun hasRealSensors(context: Context): Boolean {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager ?: return false
        val requiredSensors = listOf(
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_PROXIMITY
        )
        return requiredSensors.all { sm.getDefaultSensor(it) != null }
    }

    fun isBatteryPresent(context: Context): Boolean {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return intent?.getBooleanExtra(BatteryManager.EXTRA_PRESENT, true) == true
    }

    fun isCpuInfoSuspect(): Boolean {
        return try {
            val cpuInfo = File("/proc/cpuinfo").readText().lowercase(Locale.getDefault())
            listOf("intel", "amd", "qemu", "virtual").any { cpuInfo.contains(it) }
        } catch (e: Exception) {
            false
        }
    }

    fun hasSuspiciousInputDevices(): Boolean {
        return try {
            val ids = InputDevice.getDeviceIds()
            ids.map { InputDevice.getDevice(it) }
                .any { dev ->
                    val name = dev?.name?.lowercase(Locale.getDefault()).orEmpty()
                    name.contains("keyboard") || name.contains("mouse") || name.contains("nox")
                }
        } catch (e: Exception) {
            false
        }
    }
}