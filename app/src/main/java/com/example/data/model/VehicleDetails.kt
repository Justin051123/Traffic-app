package com.example.data.model

data class OwnerDetails(
    val firstName: String,
    val surname: String,
    val streetAddress: String,
    val suburb: String,
    val city: String,
    val province: String,
    val postalCode: String,
    val maskedId: String,
    val ownershipType: String, // e.g. "Private Individual", "Fleet / Corporate", "Commercial"
    val dateAcquired: String
) {
    val fullName: String get() = "$firstName $surname"
    val fullAddress: String get() = "$streetAddress, $suburb, $city, $postalCode"
}

data class OwnerHistoryRecord(
    val ownerNumber: Int,
    val ownerName: String,
    val ownershipType: String,
    val location: String,
    val periodYears: String,
    val startDate: String,
    val endDate: String,
    val transferReason: String
)

data class InsuranceClaim(
    val claimId: String,
    val date: String,
    val incidentType: String,
    val severity: String, // "Minor", "Moderate", "Major / Structural"
    val repairCostZar: Int,
    val sambraRepairer: String,
    val structuralFrameAffected: Boolean
)

data class AccidentInsuranceRecord(
    val hasClearHistory: Boolean,
    val majorAccidentsCount: Int,
    val floodDamageStatus: String,
    val isFloodDamaged: Boolean,
    val writeOffStatus: String, // "None (Code 2 Clean)", "Code 3 Structural Rebuilt", "Code 4 Scrap"
    val isInsuranceWriteOff: Boolean,
    val claimsHistory: List<InsuranceClaim>
)

data class RegistrationDetails(
    val licenseDiskStatus: String, // "VALID & ACTIVE", "DUE FOR RENEWAL", "EXPIRED"
    val expiryDate: String,
    val daysRemaining: Int,
    val diskBarcodeNumber: String,
    val registeringAuthorityDltc: String,
    val roadworthyCertificateNumber: String,
    val roadworthyTestDate: String
)

data class VehicleDetails(
    val vin: String,
    val make: String,
    val model: String,
    val year: Int,
    val derivative: String,
    val bodyType: String,
    val fuelType: String,
    val engineCapacityCc: String,
    val powerKw: String,
    val transmission: String,
    val drivetrain: String,
    val color: String,
    // South Africa Manufacturing & WMI
    val wmiCode: String,
    val countryOfOrigin: String,
    val assemblyPlant: String,
    // NaTIS South Africa
    val natisStatus: String,
    val registrationNumber: String,
    val province: String,
    val licenseDiscExpiry: String,
    val tareKg: Int,
    val gvmKg: Int,
    val engineNumber: String,
    // SAPS (Police) Status
    val sapsStatus: String,
    val isStolen: Boolean,
    val sapsClearanceRequired: Boolean,
    // Finance & Titleholder
    val isFinanced: Boolean,
    val titleholder: String,
    val financeInstitution: String,
    val settlementStatus: String,
    // Microdot
    val isMicrodotVerified: Boolean,
    val microdotProvider: String,
    // Condition & Accident Code
    val accidentCode: String, // Code 2 (Used), Code 3 (Built-up), Code 4 (Scrap)
    val accidentSummary: String,
    val airbagStatus: String,
    // Mileage
    val lastRecordedMileageKm: Int,
    val rollbackRisk: String,
    val serviceHistory: String,
    // Market Valuation (ZAR)
    val retailValueZar: Int,
    val tradeValueZar: Int,
    val privateValueZar: Int,
    val auctionValueZar: Int,
    // Common SA Road Notes & Issues
    val saRoadAdvisory: String,
    // Owner Details & Ownership History
    val currentOwner: OwnerDetails,
    val ownershipHistory: List<OwnerHistoryRecord>,
    // Accident & Insurance Module
    val accidentInsuranceRecord: AccidentInsuranceRecord,
    // Vehicle Registration & License Disk Database
    val registrationDetails: RegistrationDetails,
    // AI Inspection summary (can be generated or loaded, and played via TTS)
    var aiInspectionReport: String? = null
)

data class VinPreset(
    val vin: String,
    val title: String,
    val subtitle: String,
    val province: String
)
