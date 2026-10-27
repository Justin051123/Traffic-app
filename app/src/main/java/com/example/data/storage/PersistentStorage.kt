package com.example.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.data.VinDecoder
import com.example.data.model.VehicleDetails
import org.json.JSONArray
import org.json.JSONObject

/**
 * Manages device-local persistent storage using Android SharedPreferences.
 * Ensures the web app URL, scanned vehicles, and inspection records persist
 * across app closures, backgrounding, and device restarts.
 */
class PersistentStorage(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "vincheck_sa_prefs"
        const val DEFAULT_SHARED_WEB_APP_URL = "https://ais-pre-vo5tkf76v5k6zpxzeg4cii-976228587444.europe-west2.run.app"
        const val DEFAULT_DEV_WEB_APP_URL = "https://ais-dev-vo5tkf76v5k6zpxzeg4cii-976228587444.europe-west2.run.app"

        private const val KEY_WEB_APP_URL = "persistent_web_app_url"
        private const val KEY_LAST_CHECKED_VIN = "last_checked_vin"
        private const val KEY_LAST_INSPECTION_REPORT = "last_inspection_report"
        private const val KEY_SAVED_CHECKS_JSON = "saved_checks_json"
    }

    /**
     * Get the persistent Web App URL. Defaults to the Shared production app URL.
     */
    fun getWebAppUrl(): String {
        return prefs.getString(KEY_WEB_APP_URL, DEFAULT_SHARED_WEB_APP_URL) ?: DEFAULT_SHARED_WEB_APP_URL
    }

    /**
     * Save the Web App URL persistently to device storage.
     */
    fun saveWebAppUrl(url: String) {
        val cleanUrl = url.trim()
        if (cleanUrl.isNotEmpty()) {
            prefs.edit().putString(KEY_WEB_APP_URL, cleanUrl).apply()
        }
    }

    /**
     * Reset the Web App URL to the official pre-release Shared URL.
     */
    fun resetToSharedWebAppUrl() {
        saveWebAppUrl(DEFAULT_SHARED_WEB_APP_URL)
    }

    /**
     * Switch the Web App URL to the Development URL.
     */
    fun switchToDevWebAppUrl() {
        saveWebAppUrl(DEFAULT_DEV_WEB_APP_URL)
    }

    /**
     * Save the last verified vehicle details persistently.
     */
    fun saveLastVehicle(vehicle: VehicleDetails) {
        try {
            prefs.edit()
                .putString(KEY_LAST_CHECKED_VIN, vehicle.vin)
                .putString(KEY_LAST_INSPECTION_REPORT, vehicle.aiInspectionReport ?: "")
                .apply()

            // Also add to persistent history
            appendSavedCheckRecord(vehicle.vin, vehicle.registrationNumber, "${vehicle.make} ${vehicle.model} (${vehicle.year})")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Retrieve the last verified vehicle details from persistent storage.
     */
    fun getLastSavedVehicle(): VehicleDetails? {
        val vin = prefs.getString(KEY_LAST_CHECKED_VIN, null) ?: return null
        return try {
            val decoded = VinDecoder.decode(vin)
            val report = prefs.getString(KEY_LAST_INSPECTION_REPORT, null)
            if (!report.isNullOrBlank()) {
                decoded.copy(aiInspectionReport = report)
            } else {
                decoded
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun appendSavedCheckRecord(vin: String, plate: String, summary: String) {
        try {
            val existingJson = prefs.getString(KEY_SAVED_CHECKS_JSON, "[]") ?: "[]"
            val array = JSONArray(existingJson)

            // Remove if duplicate exists
            val newArray = JSONArray()
            val newEntry = JSONObject().apply {
                put("vin", vin)
                put("plate", plate)
                put("summary", summary)
                put("timestamp", System.currentTimeMillis())
            }
            newArray.put(newEntry)

            for (i in 0 until array.length()) {
                val item = array.optJSONObject(i) ?: continue
                if (item.optString("vin") != vin && newArray.length() < 20) {
                    newArray.put(item)
                }
            }
            prefs.edit().putString(KEY_SAVED_CHECKS_JSON, newArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSavedHistory(): List<Triple<String, String, String>> {
        val result = mutableListOf<Triple<String, String, String>>()
        try {
            val raw = prefs.getString(KEY_SAVED_CHECKS_JSON, "[]") ?: "[]"
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val item = array.optJSONObject(i) ?: continue
                result.add(
                    Triple(
                        item.optString("vin"),
                        item.optString("plate"),
                        item.optString("summary")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}
