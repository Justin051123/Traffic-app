package com.example.data.firebase

import android.content.Context
import com.example.R
import com.example.data.model.VehicleDetails
import com.example.data.model.VisionInspectionReport
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

data class SavedInspectionRecord(
    val id: String = "",
    val userId: String = "",
    val vin: String = "",
    val licensePlate: String = "",
    val vehicleMake: String = "",
    val vehicleModel: String = "",
    val vehicleYear: Int = 0,
    val visualRating: String = "",
    val damageSummary: String = "",
    val visibleDefects: List<String> = emptyList(),
    val verifiedFindings: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class SavedCheckRecord(
    val id: String = "",
    val userId: String = "",
    val vin: String = "",
    val licensePlate: String = "",
    val makeModel: String = "",
    val color: String = "",
    val isFinanced: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

class FirestoreVehicleRepository(
    private val firestore: FirebaseFirestore? = null,
    private val auth: FirebaseAuth? = null
) {

    constructor(context: Context) : this(
        createFirestore(context),
        createAuth()
    )

    companion object {
        private fun createFirestore(context: Context): FirebaseFirestore? {
            return try {
                val dbId = context.applicationContext.getString(R.string.firestore_database_id)
                val app = FirebaseApp.getInstance()
                FirebaseFirestore.getInstance(app, dbId)
            } catch (e: Exception) {
                try {
                    FirebaseFirestore.getInstance()
                } catch (e2: Exception) {
                    null
                }
            }
        }

        private fun createAuth(): FirebaseAuth? {
            return try {
                FirebaseAuth.getInstance()
            } catch (e: Exception) {
                null
            }
        }
    }

    fun getCurrentUserId(): String? {
        return auth?.currentUser?.uid
    }

    suspend fun saveInspection(
        report: VisionInspectionReport,
        vehicle: VehicleDetails?
    ): Result<String> {
        val fs = firestore ?: return Result.failure(Exception("Firestore not initialized"))
        return try {
            val uid = getCurrentUserId() ?: "demo_inspector_sa"
            val docRef = fs.collection("inspections").document()
            val data = hashMapOf(
                "userId" to uid,
                "vin" to (vehicle?.vin ?: report.vinAssigned ?: "UNKNOWN_VIN"),
                "licensePlate" to (vehicle?.registrationNumber ?: "UNREGISTERED"),
                "vehicleMake" to (vehicle?.make ?: "Vehicle"),
                "vehicleModel" to (vehicle?.model ?: "Unknown"),
                "vehicleYear" to (vehicle?.year ?: 2024),
                "damageSummary" to report.overallVisualCondition,
                "verificationStatus" to report.legalComplianceVerdict,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun saveVehicleCheck(vehicle: VehicleDetails): Result<String> {
        val fs = firestore ?: return Result.failure(Exception("Firestore not initialized"))
        return try {
            val uid = getCurrentUserId() ?: "demo_inspector_sa"
            val docRef = fs.collection("saved_checks").document()
            val data = hashMapOf(
                "userId" to uid,
                "vin" to vehicle.vin,
                "licensePlate" to vehicle.registrationNumber,
                "vehicleMake" to vehicle.make,
                "vehicleModel" to vehicle.model,
                "vehicleYear" to vehicle.year,
                "color" to vehicle.color,
                "isFinanced" to vehicle.isFinanced,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun savePersistentSetting(key: String, value: String): Result<Unit> {
        val fs = firestore ?: return Result.failure(Exception("Firestore not initialized"))
        val uid = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
        return try {
            val docRef = fs.collection("users").document(uid)
            docRef.set(mapOf(key to value, "updatedAt" to FieldValue.serverTimestamp())).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSavedChecks(): Flow<List<SavedCheckRecord>> = callbackFlow {
        val fs = firestore
        val uid = getCurrentUserId()
        if (fs == null || uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val subscription = fs.collection("saved_checks")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    SavedCheckRecord(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        vin = doc.getString("vin") ?: "",
                        licensePlate = doc.getString("licensePlate") ?: "",
                        makeModel = "${doc.getString("vehicleMake") ?: ""} ${doc.getString("vehicleModel") ?: ""}".trim(),
                        color = doc.getString("color") ?: "Factory Spec",
                        isFinanced = doc.getBoolean("isFinanced") ?: false,
                        timestamp = doc.getDate("createdAt")?.time ?: System.currentTimeMillis()
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { subscription.remove() }
    }
}
