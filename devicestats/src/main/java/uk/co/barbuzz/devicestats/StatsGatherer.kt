package uk.co.barbuzz.devicestats

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings.Secure
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import java.security.SecureRandom
import java.security.Security
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class StatsGatherer(private val context: Activity) {
    private val builder: StringBuilder = StringBuilder()
    val deviceStats: DeviceStats = DeviceStats()

    init {
        recordDeviceInfo()
        recordDeviceId()
        recordHardwareFeatures()
        recordScreenInfo()
        recordSupportedHTTPSCipherSuites()
        recordAlgorithms()
        recordCryptoAlgorithms()
        deviceStats.createDeviceInfoLabelMap()
        deviceStats.createHardwareInfoLabelMap()
    }

    private fun recordDeviceId() {
        var deviceId = ""
        var serial = ""
        printHeading("Device Id info")
        val telephonyManager = context
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        // might be a wifi only tablet
        if (telephonyManager != null) {
            deviceId = "NONE"//telephonyManager.deviceId
            printLine("imei-meid", deviceId)
        }

        // We're using the Reflection API because Build.SERIAL is only available
        // since API Level 9 (Gingerbread, Android 2.3).
        try {
            serial = Build::class.java.getField("SERIAL")[null] as String
            printLine("serial-number", serial)
        } catch (ignored: Exception) {
        }
        val secureId = Secure.getString(context.contentResolver, Secure.ANDROID_ID)
        printLine("secure-id", secureId)
        deviceStats!!.setDeviceId(deviceId, serial, secureId)
    }

    private fun recordHardwareFeatures() {
        printHeading("Hardware features")
        val pm = context.packageManager
        val hasNfc = pm.hasSystemFeature("android.hardware.nfc")
        val hasBluetooth = pm.hasSystemFeature("android.hardware.bluetooth")
        val hasAccel = pm.hasSystemFeature("android.hardware.sensor.accelerometer")
        val hasBarom = pm.hasSystemFeature("android.hardware.sensor.barometer")
        val hasCompass = pm.hasSystemFeature("android.hardware.sensor.compass")
        val hasGyro = pm.hasSystemFeature("android.hardware.sensor.gyroscope")
        val hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        val hasFlash = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
        val hasCameraFront = pm.hasSystemFeature("android.hardware.camera.front")
        val hasGps = pm.hasSystemFeature("android.hardware.location.gps")
        val hasNetworkLoc = pm.hasSystemFeature("android.hardware.location.network")
        deviceStats.setHardwareInfo(hasNfc, hasBluetooth, hasAccel, hasBarom, hasCompass,
                hasGyro, hasCamera, hasFlash, hasCameraFront, hasGps, hasNetworkLoc)
        printLine("Has NFC", hasNfc)
        printLine("Has bluetooth", hasBluetooth)
        printLine("Has accelerometer", hasAccel)
        printLine("Has barometer", hasBarom)
        printLine("Has compass", hasCompass)
        printLine("Has gyroscope", hasGyro)
        printLine("Has camera", hasCamera)
        printLine("Has flash", hasFlash)
        printLine("Has front camera", hasCameraFront)
        printLine("Has GPS location", hasGps)
        printLine("Has network location", hasNetworkLoc)
    }

    private fun recordScreenInfo() {
        val metrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(metrics)
        val density = metrics.densityDpi
        val xdpi = (metrics.widthPixels / metrics.density).toInt()
        val ydpi = (metrics.heightPixels / metrics.density).toInt()
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val widthInches = width / xdpi.toFloat()
        val heightInches = height / ydpi.toFloat()
        val diagInches = Math.sqrt((widthInches * widthInches
                + heightInches * heightInches).toDouble())
        val sizeQualifier = context.resources.getString(R.string.size_qualifier)
        val dpiQualifier = context.resources.getString(R.string.dpi_qualifier)
        val screenClass = context.resources.getString(R.string.screen_width_class)
        deviceStats.setScreenInfo(density, xdpi, ydpi, width, height, sizeQualifier,
                dpiQualifier, screenClass)

        //re-factored to use resource directories :-)
        printHeading("Screen Info")
        val infoStr = StringBuilder("$density dpi")
        printLine("Screen density", infoStr.toString())
        printLine("Actual dpi values", String.format(context.getString(R.string.dpi_string), xdpi, ydpi))
        printLine("Averaged dpi: ", Math.round((xdpi + ydpi) / 2.toFloat()))
        printLine(
                "Screen size", String.format("%s (%.2f\")",
                context.getString(R.string.label_screen_size), diagInches))
        printLine("Screen dimensions", String.format("(%dx%d)", width, height))
    }

    private fun recordDeviceInfo() {
        val dateFormat = SimpleDateFormat(ISO8601_FULL_DATE_STRING)
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val product = Build.PRODUCT
        val apiLevel = Build.VERSION.SDK_INT.toString()
        val release = Build.VERSION.RELEASE
        val date = dateFormat.format(Date(System.currentTimeMillis()))
        val displayName = TimeZone.getDefault().displayName
        deviceStats!!.setDeviceInfo(manufacturer, model, product, apiLevel,
                release, date, displayName)
        printHeading("Device info")
        printLine("Manufacturer", manufacturer)
        printLine("Model", model)
        //printLine("Product", product);
        printLine("API Level", apiLevel)
        printLine("Release", release)
        printLine("System Time", date)
        printLine("TimeZone", displayName)
    }

    fun printStatsToString(): String {
        return builder.toString()
    }

    private fun printArray(array: Array<String>?, newlinePerEntry: Boolean): String {
        if (array != null) {
            val b = StringBuilder()
            for (value in array) {
                b.append(value)
                if (newlinePerEntry) {
                    b.append(BR)
                } else {
                    b.append(",")
                }
            }
            return b.toString()
        }
        return ""
    }

    private fun printArray(array: List<String>?, newlinePerEntry: Boolean): String {
        if (array != null) {
            val b = StringBuilder()
            for (value in array) {
                b.append(value)
                if (newlinePerEntry) {
                    b.append(BR)
                } else {
                    b.append(",")
                }
            }
            return b.toString()
        }
        return ""
    }

    private fun printLine(label: String, value: Int) {
        printLine(label, value.toString() + "")
    }

    private fun printLine(label: String, value: Boolean) {
        printLine(label, if (value) "true" else "false")
    }

    private fun printLine(label: String, value: String?) {
        builder.append(label)
        builder.append(SEPERATOR)
        builder.append(value)
        builder.append(BR)
    }

    private fun printHeading(label: String) {
        builder.append(BR)
        builder.append(HEADING_SYM)
        builder.append(label)
        builder.append(HEADING_SYM)
        builder.append(BR)
    }

    /**
     * API 9+
     *
     * @return
     */
    @SuppressLint("NewApi")
    private fun recordSupportedHTTPSCipherSuites() {
        printHeading("SSL CipherSuites")
        try {
            val sslContext = SSLContext.getInstance("TLS")
            /*
			 * KeyManagerFactory kmf = KeyManagerFactory
			 * .getInstance(KeyManagerFactory.getDefaultAlgorithm());
			 * kmf.init(null, null);
			 *
			 * TrustManagerFactory tmf = TrustManagerFactory
			 * .getInstance(TrustManagerFactory.getDefaultAlgorithm());
			 *
			 * sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new
			 * SecureRandom());
			 */sslContext.init(null, null, SecureRandom())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                val defaultParams = sslContext.defaultSSLParameters
                val protocolsDefault = defaultParams.protocols
                val cipherSuitesDefault = defaultParams.cipherSuites
                val supportedParams = sslContext.supportedSSLParameters
                val protocolsSupport = supportedParams.protocols
                val cipherSuitesSupport = supportedParams.cipherSuites
                deviceStats!!.setSslCipherInfo(protocolsDefault, cipherSuitesDefault, protocolsSupport, cipherSuitesSupport)
                printLine("Default Protocols", printArray(protocolsDefault, false))
                printLine("Default CipherSuites", printArray(cipherSuitesDefault, true))
                printLine("Supported Protocols", printArray(protocolsSupport, false))
                printLine("Supported CipherSuites", printArray(cipherSuitesSupport, true))
            } else {
                printLine("Limited data pre API9", ":(")
                val protocolSslContext = sslContext.protocol
                printLine("Protocol", protocolSslContext)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun findX509TrustManager(tmf: TrustManagerFactory): X509TrustManager? {
        val trustManagers = tmf.trustManagers
        for (i in trustManagers.indices) {
            if (trustManagers[i] is X509TrustManager) {
                return trustManagers[i] as X509TrustManager
            }
        }
        return null
    }

    private fun recordCryptoAlgorithms() {
        printHeading("Crypto Algorithms")
        val cryptoAlgorithmsAesMap = listAlg("AES")
        val cryptoAlgorithmsEcMap = listAlg("EC")
        deviceStats.cryptoAlgorithmsAesMap = cryptoAlgorithmsAesMap
        deviceStats.cryptoAlgorithmsEcMap = cryptoAlgorithmsEcMap
    }

    private fun listAlg(algFilter: String?): Map<String, List<String>> {
        val algorithmsMap: MutableMap<String, List<String>> = LinkedHashMap()
        printLine("Algorithm Filter", algFilter)
        val providers = Security.getProviders()
        for (p in providers) {
            val providerStr = String.format("%s/%s/%f\n", p.name,
                    p.info, p.version)
            val services = p.services
            val algs: MutableList<String> = ArrayList()
            for (s in services) {
                var match = true
                if (algFilter != null) {
                    match = s.algorithm.toLowerCase()
                            .contains(algFilter.toLowerCase())
                }
                if (match) {
                    val algStr = String.format("%s/%s/%s", s.type,
                            s.algorithm, s.className)
                    algs.add(algStr)
                }
            }
            if (!algs.isEmpty()) {
                Collections.sort(algs)
                printLine("Provider", "")
                printLine(providerStr, printArray(algs, true))
                algorithmsMap[providerStr] = algs
            }
        }
        return algorithmsMap
    }

    private fun recordAlgorithms() {
        val algorithmsList: MutableList<String> = ArrayList()
        val algs = Security.getAlgorithms("cipher")
        printHeading("Ciphers")
        for (alg in algs) {
            printLine(alg, "")
            algorithmsList.add(alg)
        }
        deviceStats.algorithmsList = algorithmsList
    }

    companion object {
        private const val BR = "\n"
        private val SEPERATOR: Any = ":\t"
        private val HEADING_SYM: Any = " *** "
        private const val ISO8601_FULL_DATE_STRING = "yyyy-MM-dd HH:mm:ss"
    }
}