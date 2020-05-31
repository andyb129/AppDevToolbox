package uk.co.barbuzz.devicestats

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class DeviceStats() : Parcelable {

    private lateinit var date: String
    private lateinit var manufacturer: String
    private lateinit var product: String
    private lateinit var apiLevel: String
    private lateinit var release: String
    private lateinit var imei: String
    private lateinit var serialNumber: String
    private lateinit var secureId: String
    private var hasNFC = false
    private var hasBluetooth = false
    private var hasAccel = false
    private var hasBarom = false
    private var hasCompass = false
    private var hasGyro = false
    private var hasCamera = false
    private var hasFlash = false
    private var hasCameraFront = false
    private var hasGps = false
    private var hasNetworkLoc = false
    private var brand: String = ""
    private var timeZone: String = ""
    var model: String = ""

    private var ciphersList: List<String> = emptyList()
    private var sslCiphersList: List<String> = emptyList()
    private var sslCiphersProtocolsList: List<String> = emptyList()
    private var protocolsList: List<String> = emptyList()
    private var providerList: List<String> = emptyList()
    var deviceInfoLabelMap: LinkedHashMap<String, String> = linkedMapOf()
    var hardwareLabelMap: LinkedHashMap<String, String>  = linkedMapOf()
    var protocolsSupport: Array<String> = emptyArray()
    var protocolsDefault: Array<String> = emptyArray()
    var cipherSuitesSupport: Array<String> = emptyArray()
    var cipherSuitesDefault: Array<String> = emptyArray()
    var algorithmsList: List<String> = emptyList()
    var cryptoAlgorithmsAesMap: Map<String, List<String>> = emptyMap()
    var cryptoAlgorithmsEcMap: Map<String, List<String>> = emptyMap()
    var density = 0
    var widthDpi = 0
    var heightDpi = 0
    var width = 0
    var height = 0
    private lateinit var sizeQualifier: String
    private lateinit var dpiQualifier: String
    private lateinit var screenClass: String

    constructor(parcel: Parcel) : this() {
        date = parcel.readString()!!
        timeZone = parcel.readString()!!
        manufacturer = parcel.readString()!!
        product = parcel.readString()!!
        apiLevel = parcel.readString()!!
        release = parcel.readString()!!
        brand = parcel.readString()!!
        imei = parcel.readString()!!
        serialNumber = parcel.readString()!!
        secureId = parcel.readString()!!
        hasNFC = parcel.readByte() != 0.toByte()
        hasBluetooth = parcel.readByte() != 0.toByte()
        hasAccel = parcel.readByte() != 0.toByte()
        hasBarom = parcel.readByte() != 0.toByte()
        hasCompass = parcel.readByte() != 0.toByte()
        hasGyro = parcel.readByte() != 0.toByte()
        hasCamera = parcel.readByte() != 0.toByte()
        hasFlash = parcel.readByte() != 0.toByte()
        hasCameraFront = parcel.readByte() != 0.toByte()
        hasGps = parcel.readByte() != 0.toByte()
        hasNetworkLoc = parcel.readByte() != 0.toByte()
        model = parcel.readString()!!
        ciphersList = parcel.createStringArrayList()!!
        sslCiphersList = parcel.createStringArrayList()!!
        sslCiphersProtocolsList = parcel.createStringArrayList()!!
        protocolsList = parcel.createStringArrayList()!!
        providerList = parcel.createStringArrayList()!!
        protocolsDefault = parcel.createStringArray()!!
        cipherSuitesDefault = parcel.createStringArray()!!
        protocolsSupport = parcel.createStringArray()!!
        cipherSuitesSupport = parcel.createStringArray()!!
        algorithmsList = parcel.createStringArrayList()!!
        density = parcel.readInt()
        widthDpi = parcel.readInt()
        heightDpi = parcel.readInt()
        width = parcel.readInt()
        height = parcel.readInt()
        sizeQualifier = parcel.readString()!!
        dpiQualifier = parcel.readString()!!
        screenClass = parcel.readString()!!
    }

    interface StatsLabels {
        companion object {
            const val DATE_LABEL = "TIME"
            const val TIME_ZONE_LABEL = "TIMEZONE"
            const val MANUFACTURER_LABEL = "MANUFACTURER"
            const val MODEL_LABEL = "MODEL"
            const val API_LEVEL_LABEL = "API LEVEL"
            const val RELEASE_LABEL = "RELEASE"
            const val BRAND_LABEL = "BRAND"
            const val IEMI_LABEL = "IEMI"
            const val SERIAL_LABEL = "SERIAL NO"
            const val SECURE_ID_LABEL = "SECURE ID"
            const val IS_NFC_LABEL = "NFC"
            const val IS_BLUETOOTH_LABEL = "BLUETOOTH"
            const val IS_ACCELEROMETER_LABEL = "ACCELEROMETER"
            const val IS_BAROMETER_LABEL = "BAROMETER"
            const val IS_COMPASS_LABEL = "COMPASS"
            const val IS_GYROSCOPE_LABEL = "GYROSCOPE"
            const val IS_CAMERA_LABEL = "CAMERA"
            const val IS_FLASH_LABEL = "FLASH"
            const val IS_CAMERA_FRONT_LABEL = "CAMERA FRONT"
            const val IS_GPS_LABEL = "GPS"
            const val IS_NETWORK_LOCATION_LABEL = "NETWORK LOCATION"
        }
    }

    // convenience methods to set data
    fun setScreenInfo(density: Int, widthDpi: Int, heightDpi: Int, width: Int, height: Int,
                      sizeQualifier: String, dpiQualifier: String, screenClass: String) {
        this.density = density
        this.widthDpi = widthDpi
        this.heightDpi = heightDpi
        this.width = width
        this.height = height
        this.sizeQualifier = sizeQualifier
        this.dpiQualifier = dpiQualifier
        this.screenClass = screenClass
    }

    fun setDeviceInfo(manufacturer: String, model: String, product: String, apiLevel: String,
                      release: String, date: String, displayName: String) {
        this.manufacturer = manufacturer
        this.model = model
        this.product = product
        this.apiLevel = apiLevel
        this.release = release
        this.date = date
    }

    fun setDeviceId(deviceId: String, serial: String, secureId: String) {
        imei = deviceId
        serialNumber = serial
        this.secureId = secureId
    }

    fun setHardwareInfo(hasNFC: Boolean, hasBluetooth: Boolean, hasAccel: Boolean,
                        hasBarom: Boolean, hasCompass: Boolean, hasGyro: Boolean, hasCamera: Boolean,
                        hasFlash: Boolean, hasCameraFront: Boolean, hasGps: Boolean, hasNetworkLoc: Boolean) {
        this.hasNFC = hasNFC
        this.hasBluetooth = hasBluetooth
        this.hasAccel = hasAccel
        this.hasBarom = hasBarom
        this.hasCompass = hasCompass
        this.hasGyro = hasGyro
        this.hasCamera = hasCamera
        this.hasFlash = hasFlash
        this.hasCameraFront = hasCameraFront
        this.hasGps = hasGps
        this.hasNetworkLoc = hasNetworkLoc
    }

    fun createDeviceInfoLabelMap() {
        deviceInfoLabelMap = LinkedHashMap()
        deviceInfoLabelMap[StatsLabels.DATE_LABEL] = date
        deviceInfoLabelMap[StatsLabels.TIME_ZONE_LABEL] = timeZone
        deviceInfoLabelMap[StatsLabels.MANUFACTURER_LABEL] = manufacturer
        deviceInfoLabelMap[StatsLabels.MODEL_LABEL] = model
        deviceInfoLabelMap[StatsLabels.API_LEVEL_LABEL] = apiLevel
        deviceInfoLabelMap[StatsLabels.RELEASE_LABEL] = release
        deviceInfoLabelMap[StatsLabels.BRAND_LABEL] = brand
        deviceInfoLabelMap[StatsLabels.IEMI_LABEL] = imei
        deviceInfoLabelMap[StatsLabels.SERIAL_LABEL] = serialNumber
        deviceInfoLabelMap[StatsLabels.SECURE_ID_LABEL] = secureId
        deviceInfoLabelMap = deviceInfoLabelMap
    }

    fun createHardwareInfoLabelMap() {
        hardwareLabelMap = LinkedHashMap()
        hardwareLabelMap[StatsLabels.IS_NFC_LABEL] = if (hasNFC) HAS_HARDWARE else NO_HARDWARE
        hardwareLabelMap[StatsLabels.IS_BLUETOOTH_LABEL] = if (hasBluetooth) HAS_HARDWARE else NO_HARDWARE
        hardwareLabelMap[StatsLabels.IS_ACCELEROMETER_LABEL] = if (hasAccel) HAS_HARDWARE else NO_HARDWARE
        hardwareLabelMap[StatsLabels.IS_BAROMETER_LABEL] = if (hasBluetooth) HAS_HARDWARE else NO_HARDWARE
        hardwareLabelMap[StatsLabels.IS_COMPASS_LABEL] = if (hasBluetooth) HAS_HARDWARE else NO_HARDWARE
        hardwareLabelMap[StatsLabels.IS_GYROSCOPE_LABEL] = if (hasBluetooth) HAS_HARDWARE else NO_HARDWARE
        hardwareLabelMap[StatsLabels.IS_CAMERA_LABEL] = if (hasBluetooth) HAS_HARDWARE else NO_HARDWARE
        hardwareLabelMap[StatsLabels.IS_FLASH_LABEL] = if (hasBluetooth) HAS_HARDWARE else NO_HARDWARE
        hardwareLabelMap[StatsLabels.IS_CAMERA_FRONT_LABEL] = if (hasBluetooth) HAS_HARDWARE else NO_HARDWARE
        hardwareLabelMap[StatsLabels.IS_GPS_LABEL] = if (hasBluetooth) HAS_HARDWARE else NO_HARDWARE
        hardwareLabelMap[StatsLabels.IS_NETWORK_LOCATION_LABEL] = if (hasBluetooth) HAS_HARDWARE else NO_HARDWARE
        hardwareLabelMap = hardwareLabelMap
    }

    fun setSslCipherInfo(protocolsDefault: Array<String>, cipherSuitesDefault: Array<String>,
                         protocolsSupport: Array<String>, cipherSuitesSupport: Array<String>) {
        this.protocolsDefault = protocolsDefault
        this.cipherSuitesDefault = cipherSuitesDefault
        this.protocolsSupport = protocolsSupport
        this.cipherSuitesSupport = cipherSuitesSupport
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
        parcel.writeString(timeZone)
        parcel.writeString(manufacturer)
        parcel.writeString(product)
        parcel.writeString(apiLevel)
        parcel.writeString(release)
        parcel.writeString(brand)
        parcel.writeString(imei)
        parcel.writeString(serialNumber)
        parcel.writeString(secureId)
        parcel.writeByte(if (hasNFC) 1 else 0)
        parcel.writeByte(if (hasBluetooth) 1 else 0)
        parcel.writeByte(if (hasAccel) 1 else 0)
        parcel.writeByte(if (hasBarom) 1 else 0)
        parcel.writeByte(if (hasCompass) 1 else 0)
        parcel.writeByte(if (hasGyro) 1 else 0)
        parcel.writeByte(if (hasCamera) 1 else 0)
        parcel.writeByte(if (hasFlash) 1 else 0)
        parcel.writeByte(if (hasCameraFront) 1 else 0)
        parcel.writeByte(if (hasGps) 1 else 0)
        parcel.writeByte(if (hasNetworkLoc) 1 else 0)
        parcel.writeString(model)
        parcel.writeStringList(ciphersList)
        parcel.writeStringList(sslCiphersList)
        parcel.writeStringList(sslCiphersProtocolsList)
        parcel.writeStringList(protocolsList)
        parcel.writeStringList(providerList)
        parcel.writeStringArray(protocolsDefault)
        parcel.writeStringArray(cipherSuitesDefault)
        parcel.writeStringArray(protocolsSupport)
        parcel.writeStringArray(cipherSuitesSupport)
        parcel.writeStringList(algorithmsList)
        parcel.writeInt(density)
        parcel.writeInt(widthDpi)
        parcel.writeInt(heightDpi)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(sizeQualifier)
        parcel.writeString(dpiQualifier)
        parcel.writeString(screenClass)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DeviceStats> {
        const val HAS_HARDWARE = "Yes"
        const val NO_HARDWARE = "No"

        override fun createFromParcel(parcel: Parcel): DeviceStats {
            return DeviceStats(parcel)
        }

        override fun newArray(size: Int): Array<DeviceStats?> {
            return arrayOfNulls(size)
        }
    }
}
