package com.example.data.model

import android.graphics.Bitmap

data class VisionInspectionReport(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val detectedVehicleDescription: String,
    val overallVisualCondition: String, // "Good / Roadworthy Appearance", "Fair / Cosmetic Wear", "Damaged / Defect Observed"
    val verifiedVisualFindings: List<String>,
    val visibleDefects: List<String>,
    val unverifiedAreas: List<String>, // Areas not visible and strictly withheld from speculation
    val legalComplianceVerdict: String,
    val fullReportText: String,
    val photoBitmap: Bitmap? = null,
    val vinAssigned: String? = null
)
