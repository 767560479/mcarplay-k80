package com.mcarplay.app.ui

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.TextView
import com.mcarplay.app.BuildConfig
import com.mcarplay.app.R
import com.mcarplay.app.carlife.CarLifeLauncher
import com.mcarplay.app.diagnostics.ApplicationAvailability
import com.mcarplay.app.diagnostics.ApplicationInstallation
import com.mcarplay.app.diagnostics.DeviceReadiness
import com.mcarplay.app.util.Bg

class DeviceSetupCard(
    private val activity: Activity,
    onSelfTest: () -> Unit,
) {
    private val summary = activity.findViewById<TextView>(R.id.deviceSetupSummary)
    private val selfTest = activity.findViewById<View>(R.id.btnQuickCarWithSelfTest)
    private var readiness: DeviceReadiness? = null
    private var generation = 0

    init {
        selfTest.isEnabled = false
        selfTest.setOnClickListener { onSelfTest() }
        activity.findViewById<View>(R.id.btnDeviceReadiness).setOnClickListener { showDetails() }
    }

    fun refresh() {
        val request = ++generation
        val context = activity.applicationContext
        Bg.run("device-readiness") {
            val result =
                DeviceReadiness(
                    carWith = installation(context, CarLifeLauncher.CARWITH_PACKAGE),
                    carLife = installation(context, CarLifeLauncher.CARLIFE_PACKAGE),
                    amap = installation(context, DeviceReadiness.AMAP_PACKAGE),
                    music = installation(context, DeviceReadiness.MUSIC_PACKAGE),
                )
            activity.runOnUiThread {
                if (!activity.isFinishing && !activity.isDestroyed && request == generation) {
                    readiness = result
                    summary.text =
                        activity.getString(
                            R.string.setup_summary,
                            describe(result.carWith),
                            describe(result.carLife),
                            describe(result.amap),
                            describe(result.music),
                        )
                    selfTest.isEnabled = result.canSelfTest
                    selfTest.alpha = if (result.canSelfTest) 1f else 0.45f
                }
            }
        }
    }

    private fun showDetails() {
        val result = readiness ?: return
        val message =
            buildString {
                appendLine(activity.getString(R.string.setup_current_device, Build.MANUFACTURER, Build.MODEL))
                appendLine(activity.getString(R.string.setup_current_system, Build.VERSION.RELEASE, Build.VERSION.INCREMENTAL))
                appendLine()
                appendLine(
                    activity.getString(
                        R.string.setup_summary,
                        describe(result.carWith),
                        describe(result.carLife),
                        describe(result.amap),
                        describe(result.music),
                    ),
                )
                appendLine()
                appendLine(activity.getString(R.string.setup_reference))
                appendLine(
                    activity.getString(
                        if (result.carWithMatchesReference) R.string.setup_version_match else R.string.setup_version_other,
                    ),
                )
                if (!result.canSelfTest) appendLine(activity.getString(R.string.setup_components_required))
                appendLine()
                append(activity.getString(R.string.setup_validation_steps))
            }
        DarkMessageDialog.show(
            activity,
            activity.getString(R.string.setup_details),
            message,
            listOf(DarkMessageDialog.Action(activity.getString(android.R.string.ok)) {}),
        )
    }

    fun showLocalBuildInfo() {
        DarkMessageDialog.show(
            activity,
            activity.getString(R.string.setup_local_build_title),
            activity.getString(R.string.setup_local_build_message, BuildConfig.VERSION_NAME),
            listOf(DarkMessageDialog.Action(activity.getString(android.R.string.ok)) {}),
        )
    }

    private fun describe(installation: ApplicationInstallation): String =
        when (installation.availability) {
            ApplicationAvailability.AVAILABLE -> installation.versionName ?: activity.getString(R.string.setup_installed)
            ApplicationAvailability.DISABLED -> activity.getString(R.string.setup_disabled)
            ApplicationAvailability.MISSING -> activity.getString(R.string.setup_missing)
            ApplicationAvailability.UNKNOWN -> activity.getString(R.string.setup_unknown)
        }

    @Suppress("DEPRECATION")
    private fun installation(
        context: Context,
        packageName: String,
    ): ApplicationInstallation =
        try {
            val info = context.packageManager.getPackageInfo(packageName, 0)
            val enabled = info.applicationInfo?.enabled == true
            ApplicationInstallation(
                if (enabled) ApplicationAvailability.AVAILABLE else ApplicationAvailability.DISABLED,
                info.versionName,
            )
        } catch (_: PackageManager.NameNotFoundException) {
            ApplicationInstallation(ApplicationAvailability.MISSING)
        } catch (_: RuntimeException) {
            ApplicationInstallation(ApplicationAvailability.UNKNOWN)
        }
}
