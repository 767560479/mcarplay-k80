package com.mcarplay.app.diagnostics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceReadinessTest {
    private val available = ApplicationInstallation(ApplicationAvailability.AVAILABLE, DeviceReadiness.REFERENCE_CARWITH_VERSION)
    private val missing = ApplicationInstallation(ApplicationAvailability.MISSING)

    @Test
    fun selfTestRequiresBothEnabledSourceComponents() {
        for (carWithState in ApplicationAvailability.entries) {
            for (carLifeState in ApplicationAvailability.entries) {
                val readiness =
                    DeviceReadiness(
                        ApplicationInstallation(carWithState),
                        ApplicationInstallation(carLifeState),
                        missing,
                        missing,
                    )
                assertEquals(
                    "$carWithState / $carLifeState",
                    carWithState == ApplicationAvailability.AVAILABLE && carLifeState == ApplicationAvailability.AVAILABLE,
                    readiness.canSelfTest,
                )
            }
        }
    }

    @Test
    fun absentOptionalAppsDoNotBlockSourceSelfTest() {
        assertTrue(DeviceReadiness(available, available, missing, missing).canSelfTest)
    }

    @Test
    fun exactReferenceVersionIsRecognized() {
        assertTrue(DeviceReadiness(available, available, missing, missing).carWithMatchesReference)
    }

    @Test
    fun similarVersionStringsDoNotCountAsReferenceMatch() {
        for (version in listOf("4.0.21-20260929", "4.0.23-20261016", "4.0.21-20261016-beta", null)) {
            val installation = ApplicationInstallation(ApplicationAvailability.AVAILABLE, version)
            assertFalse(DeviceReadiness(installation, available, missing, missing).carWithMatchesReference)
        }
    }

    @Test
    fun differentVersionDoesNotPreventCollectingRealStreamingEvidence() {
        val different = ApplicationInstallation(ApplicationAvailability.AVAILABLE, "4.0.23-20261016")
        assertTrue(DeviceReadiness(different, available, missing, missing).canSelfTest)
    }

    @Test
    fun disabledReferenceInstallationDoesNotCountAsReady() {
        val disabled = available.copy(availability = ApplicationAvailability.DISABLED)
        val readiness = DeviceReadiness(disabled, available, missing, missing)
        assertFalse(readiness.canSelfTest)
        assertFalse(readiness.carWithMatchesReference)
    }
}
