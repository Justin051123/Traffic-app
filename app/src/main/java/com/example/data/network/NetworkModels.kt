package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Objects for Public Vehicle APIs (NHTSA VPIC) and
 * South African National Traffic Information System (NaTIS) vehicle database.
 */

@JsonClass(generateAdapter = true)
data class VpicResponse(
    @Json(name = "Count") val count: Int? = null,
    @Json(name = "Message") val message: String? = null,
    @Json(name = "Results") val results: List<VpicVehicleResult>? = null
)

@JsonClass(generateAdapter = true)
data class VpicVehicleResult(
    @Json(name = "VIN") val vin: String? = null,
    @Json(name = "Make") val make: String? = null,
    @Json(name = "Model") val model: String? = null,
    @Json(name = "ModelYear") val modelYear: String? = null,
    @Json(name = "Manufacturer") val manufacturer: String? = null,
    @Json(name = "PlantCountry") val plantCountry: String? = null,
    @Json(name = "PlantCity") val plantCity: String? = null,
    @Json(name = "PlantCompanyName") val plantCompanyName: String? = null,
    @Json(name = "VehicleType") val vehicleType: String? = null,
    @Json(name = "BodyClass") val bodyClass: String? = null,
    @Json(name = "DisplacementL") val displacementL: String? = null,
    @Json(name = "EngineCylinders") val engineCylinders: String? = null,
    @Json(name = "FuelTypePrimary") val fuelTypePrimary: String? = null,
    @Json(name = "DriveType") val driveType: String? = null,
    @Json(name = "TransmissionStyle") val transmissionStyle: String? = null,
    @Json(name = "ErrorCode") val errorCode: String? = null,
    @Json(name = "ErrorText") val errorText: String? = null
)

@JsonClass(generateAdapter = true)
data class SaNatisVehicleResponse(
    @Json(name = "statusCode") val statusCode: Int = 200,
    @Json(name = "statusMessage") val statusMessage: String = "SUCCESS",
    @Json(name = "queryTimestamp") val queryTimestamp: String? = null,
    @Json(name = "dataSource") val dataSource: String = "NaTIS South Africa Live Gateway",
    @Json(name = "data") val data: SaNatisVehicleData? = null
)

@JsonClass(generateAdapter = true)
data class SaNatisVehicleData(
    @Json(name = "vin") val vin: String,
    @Json(name = "registrationNumber") val registrationNumber: String,
    @Json(name = "make") val make: String,
    @Json(name = "model") val model: String,
    @Json(name = "year") val year: Int,
    @Json(name = "derivative") val derivative: String? = null,
    @Json(name = "bodyType") val bodyType: String? = null,
    @Json(name = "fuelType") val fuelType: String? = null,
    @Json(name = "engineCapacityCc") val engineCapacityCc: String? = null,
    @Json(name = "powerKw") val powerKw: String? = null,
    @Json(name = "transmission") val transmission: String? = null,
    @Json(name = "drivetrain") val drivetrain: String? = null,
    @Json(name = "color") val color: String? = null,
    @Json(name = "wmiCode") val wmiCode: String? = null,
    @Json(name = "countryOfOrigin") val countryOfOrigin: String? = null,
    @Json(name = "assemblyPlant") val assemblyPlant: String? = null,
    @Json(name = "natisStatus") val natisStatus: String,
    @Json(name = "province") val province: String,
    @Json(name = "licenseDiscExpiry") val licenseDiscExpiry: String,
    @Json(name = "tareKg") val tareKg: Int? = null,
    @Json(name = "gvmKg") val gvmKg: Int? = null,
    @Json(name = "engineNumber") val engineNumber: String? = null,
    @Json(name = "sapsStatus") val sapsStatus: String? = null,
    @Json(name = "isStolen") val isStolen: Boolean = false,
    @Json(name = "sapsClearanceRequired") val sapsClearanceRequired: Boolean = false,
    @Json(name = "isFinanced") val isFinanced: Boolean = false,
    @Json(name = "titleholder") val titleholder: String? = null,
    @Json(name = "financeInstitution") val financeInstitution: String? = null,
    @Json(name = "settlementStatus") val settlementStatus: String? = null,
    @Json(name = "isMicrodotVerified") val isMicrodotVerified: Boolean = true,
    @Json(name = "microdotProvider") val microdotProvider: String? = null,
    @Json(name = "accidentCode") val accidentCode: String? = null,
    @Json(name = "accidentSummary") val accidentSummary: String? = null,
    @Json(name = "airbagStatus") val airbagStatus: String? = null,
    @Json(name = "lastRecordedMileageKm") val lastRecordedMileageKm: Int? = null,
    @Json(name = "rollbackRisk") val rollbackRisk: String? = null,
    @Json(name = "serviceHistory") val serviceHistory: String? = null,
    @Json(name = "retailValueZar") val retailValueZar: Int? = null,
    @Json(name = "tradeValueZar") val tradeValueZar: Int? = null,
    @Json(name = "privateValueZar") val privateValueZar: Int? = null,
    @Json(name = "auctionValueZar") val auctionValueZar: Int? = null,
    @Json(name = "saRoadAdvisory") val saRoadAdvisory: String? = null,
    @Json(name = "currentOwner") val currentOwner: SaOwnerDto? = null,
    @Json(name = "ownershipHistory") val ownershipHistory: List<SaOwnerHistoryDto>? = null,
    @Json(name = "accidentInsurance") val accidentInsurance: SaAccidentInsuranceDto? = null,
    @Json(name = "registrationDetails") val registrationDetails: SaRegistrationDetailsDto? = null
)

@JsonClass(generateAdapter = true)
data class SaOwnerDto(
    @Json(name = "firstName") val firstName: String,
    @Json(name = "surname") val surname: String,
    @Json(name = "streetAddress") val streetAddress: String,
    @Json(name = "suburb") val suburb: String,
    @Json(name = "city") val city: String,
    @Json(name = "province") val province: String,
    @Json(name = "postalCode") val postalCode: String,
    @Json(name = "maskedId") val maskedId: String,
    @Json(name = "ownershipType") val ownershipType: String,
    @Json(name = "dateAcquired") val dateAcquired: String
)

@JsonClass(generateAdapter = true)
data class SaOwnerHistoryDto(
    @Json(name = "ownerNumber") val ownerNumber: Int,
    @Json(name = "ownerName") val ownerName: String,
    @Json(name = "ownershipType") val ownershipType: String,
    @Json(name = "location") val location: String,
    @Json(name = "periodYears") val periodYears: String,
    @Json(name = "startDate") val startDate: String,
    @Json(name = "endDate") val endDate: String,
    @Json(name = "transferReason") val transferReason: String
)

@JsonClass(generateAdapter = true)
data class SaAccidentInsuranceDto(
    @Json(name = "hasClearHistory") val hasClearHistory: Boolean = true,
    @Json(name = "majorAccidentsCount") val majorAccidentsCount: Int = 0,
    @Json(name = "floodDamageStatus") val floodDamageStatus: String = "No flood history",
    @Json(name = "isFloodDamaged") val isFloodDamaged: Boolean = false,
    @Json(name = "writeOffStatus") val writeOffStatus: String = "None (Code 2 Clean)",
    @Json(name = "isInsuranceWriteOff") val isInsuranceWriteOff: Boolean = false,
    @Json(name = "claimsHistory") val claimsHistory: List<SaInsuranceClaimDto>? = null
)

@JsonClass(generateAdapter = true)
data class SaInsuranceClaimDto(
    @Json(name = "claimId") val claimId: String,
    @Json(name = "date") val date: String,
    @Json(name = "incidentType") val incidentType: String,
    @Json(name = "severity") val severity: String,
    @Json(name = "repairCostZar") val repairCostZar: Int,
    @Json(name = "sambraRepairer") val sambraRepairer: String,
    @Json(name = "structuralFrameAffected") val structuralFrameAffected: Boolean
)

@JsonClass(generateAdapter = true)
data class SaRegistrationDetailsDto(
    @Json(name = "licenseDiskStatus") val licenseDiskStatus: String,
    @Json(name = "expiryDate") val expiryDate: String,
    @Json(name = "daysRemaining") val daysRemaining: Int,
    @Json(name = "diskBarcodeNumber") val diskBarcodeNumber: String,
    @Json(name = "registeringAuthorityDltc") val registeringAuthorityDltc: String,
    @Json(name = "roadworthyCertificateNumber") val roadworthyCertificateNumber: String,
    @Json(name = "roadworthyTestDate") val roadworthyTestDate: String
)
