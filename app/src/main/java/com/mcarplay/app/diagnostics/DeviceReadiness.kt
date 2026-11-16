package com.mcarplay.app.diagnostics

enum class ApplicationAvailability { AVAILABLE, DISABLED, MISSING, UNKNOWN }

data class ApplicationInstallation(
    val availability: ApplicationAvailability,
    val versionName: String? = null,
)

data class DeviceReadiness(
    val carWith: ApplicationInstallation,
    val carLife: ApplicationInstallation,
    val amap: ApplicationInstallation,
    val music: ApplicationInstallation,
) {
    val canSelfTest: Boolean
        get() = carWith.availability == ApplicationAvailability.AVAILABLE && carLife.availability == ApplicationAvailability.AVAILABLE

    val carWithMatchesReference: Boolean
        get() = carWith.availability == ApplicationAvailability.AVAILABLE && carWith.versionName == REFERENCE_CARWITH_VERSION

    companion object {
        const val REFERENCE_CARWITH_VERSION = "4.0.21-20261016"
        const val AMAP_PACKAGE = "com.autonavi.minimap"
        const val MUSIC_PACKAGE = "com.netease.cloudmusic"
    }
}
