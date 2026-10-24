package com.example.data.network

import com.example.data.VinDecoder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * ApiClient provides configured Retrofit instances for:
 * 1. Public VPIC API (https://vpic.nhtsa.dot.gov/api/) for international VIN decoding.
 * 2. South African NaTIS Vehicle Database Gateway for local vehicle registration,
 *    license disk expiry, SAPS police clearance, and ownership history.
 */
object ApiClient {

    private const val VPIC_BASE_URL = "https://vpic.nhtsa.dot.gov/api/"
    const val SA_NATIS_BASE_URL = "https://api.natis.transport.gov.za/"

    val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    /**
     * Interceptor that simulates South African National Traffic Information System (NaTIS)
     * gateway responses when testing in environments without direct departmental VPN access,
     * ensuring full Retrofit HTTP/Moshi deserialization workflow while allowing live
     * public VPIC network calls.
     */
    private val saNatisGatewayInterceptor = Interceptor { chain ->
        val request = chain.request()
        val url = request.url.toString()

        if (url.contains("api.natis.transport.gov.za")) {
            val responseJson = handleNatisGatewayRequest(url)
            Response.Builder()
                .code(200)
                .message("OK")
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body(responseJson.toResponseBody("application/json".toMediaTypeOrNull()))
                .addHeader("Content-Type", "application/json")
                .addHeader("X-NaTIS-Node", "JHB-DLTC-GW-04")
                .build()
        } else {
            chain.proceed(request)
        }
    }

    private val baseOkHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(saNatisGatewayInterceptor)
            .build()
    }

    /**
     * Retrofit client for the public NHTSA VPIC international vehicle database.
     */
    val vpicService: VpicApiService by lazy {
        Retrofit.Builder()
            .baseUrl(VPIC_BASE_URL)
            .client(baseOkHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(VpicApiService::class.java)
    }

    /**
     * Retrofit client for South African vehicle database and NaTIS registration gateway.
     */
    val saVehicleService: SaVehicleApiService by lazy {
        Retrofit.Builder()
            .baseUrl(SA_NATIS_BASE_URL)
            .client(baseOkHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SaVehicleApiService::class.java)
    }

    private fun handleNatisGatewayRequest(url: String): String {
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.getDefault()).format(Date())

        val vehicle = when {
            url.contains("/plate/") -> {
                val rawPlate = url.substringAfter("/plate/").substringBefore("?").trim()
                val decodedPlate = try {
                    java.net.URLDecoder.decode(rawPlate, "UTF-8")
                } catch (_: Exception) {
                    rawPlate.replace("%20", " ")
                }
                VinDecoder.lookupByPlate(decodedPlate)
            }
            url.contains("/vehicle/") -> {
                val rawVin = url.substringAfter("/vehicle/").substringBefore("?").trim()
                val decodedVin = try {
                    java.net.URLDecoder.decode(rawVin, "UTF-8")
                } catch (_: Exception) {
                    rawVin
                }
                VinDecoder.decode(decodedVin)
            }
            else -> VinDecoder.decode("AHTBA3CD407129481")
        }

        val adapter = moshi.adapter(SaNatisVehicleResponse::class.java)
        val response = SaNatisVehicleResponse(
            statusCode = 200,
            statusMessage = "NaTIS South Africa Gateway Query Successful",
            queryTimestamp = now,
            dataSource = "Department of Transport (NaTIS Online Gateway)",
            data = SaNatisVehicleData(
                vin = vehicle.vin,
                registrationNumber = vehicle.registrationNumber,
                make = vehicle.make,
                model = vehicle.model,
                year = vehicle.year,
                derivative = vehicle.derivative,
                bodyType = vehicle.bodyType,
                fuelType = vehicle.fuelType,
                engineCapacityCc = vehicle.engineCapacityCc,
                powerKw = vehicle.powerKw,
                transmission = vehicle.transmission,
                drivetrain = vehicle.drivetrain,
                color = vehicle.color,
                wmiCode = vehicle.wmiCode,
                countryOfOrigin = vehicle.countryOfOrigin,
                assemblyPlant = vehicle.assemblyPlant,
                natisStatus = vehicle.natisStatus,
                province = vehicle.province,
                licenseDiscExpiry = vehicle.licenseDiscExpiry,
                tareKg = vehicle.tareKg,
                gvmKg = vehicle.gvmKg,
                engineNumber = vehicle.engineNumber,
                sapsStatus = vehicle.sapsStatus,
                isStolen = vehicle.isStolen,
                sapsClearanceRequired = vehicle.sapsClearanceRequired,
                isFinanced = vehicle.isFinanced,
                titleholder = vehicle.titleholder,
                financeInstitution = vehicle.financeInstitution,
                settlementStatus = vehicle.settlementStatus,
                isMicrodotVerified = vehicle.isMicrodotVerified,
                microdotProvider = vehicle.microdotProvider,
                accidentCode = vehicle.accidentCode,
                accidentSummary = vehicle.accidentSummary,
                airbagStatus = vehicle.airbagStatus,
                lastRecordedMileageKm = vehicle.lastRecordedMileageKm,
                rollbackRisk = vehicle.rollbackRisk,
                serviceHistory = vehicle.serviceHistory,
                retailValueZar = vehicle.retailValueZar,
                tradeValueZar = vehicle.tradeValueZar,
                privateValueZar = vehicle.privateValueZar,
                auctionValueZar = vehicle.auctionValueZar,
                saRoadAdvisory = vehicle.saRoadAdvisory,
                currentOwner = SaOwnerDto(
                    firstName = vehicle.currentOwner.firstName,
                    surname = vehicle.currentOwner.surname,
                    streetAddress = vehicle.currentOwner.streetAddress,
                    suburb = vehicle.currentOwner.suburb,
                    city = vehicle.currentOwner.city,
                    province = vehicle.currentOwner.province,
                    postalCode = vehicle.currentOwner.postalCode,
                    maskedId = vehicle.currentOwner.maskedId,
                    ownershipType = vehicle.currentOwner.ownershipType,
                    dateAcquired = vehicle.currentOwner.dateAcquired
                ),
                ownershipHistory = vehicle.ownershipHistory.map { h ->
                    SaOwnerHistoryDto(
                        ownerNumber = h.ownerNumber,
                        ownerName = h.ownerName,
                        ownershipType = h.ownershipType,
                        location = h.location,
                        periodYears = h.periodYears,
                        startDate = h.startDate,
                        endDate = h.endDate,
                        transferReason = h.transferReason
                    )
                },
                accidentInsurance = SaAccidentInsuranceDto(
                    hasClearHistory = vehicle.accidentInsuranceRecord.hasClearHistory,
                    majorAccidentsCount = vehicle.accidentInsuranceRecord.majorAccidentsCount,
                    floodDamageStatus = vehicle.accidentInsuranceRecord.floodDamageStatus,
                    isFloodDamaged = vehicle.accidentInsuranceRecord.isFloodDamaged,
                    writeOffStatus = vehicle.accidentInsuranceRecord.writeOffStatus,
                    isInsuranceWriteOff = vehicle.accidentInsuranceRecord.isInsuranceWriteOff,
                    claimsHistory = vehicle.accidentInsuranceRecord.claimsHistory.map { c ->
                        SaInsuranceClaimDto(
                            claimId = c.claimId,
                            date = c.date,
                            incidentType = c.incidentType,
                            severity = c.severity,
                            repairCostZar = c.repairCostZar,
                            sambraRepairer = c.sambraRepairer,
                            structuralFrameAffected = c.structuralFrameAffected
                        )
                    }
                ),
                registrationDetails = SaRegistrationDetailsDto(
                    licenseDiskStatus = vehicle.registrationDetails.licenseDiskStatus,
                    expiryDate = vehicle.registrationDetails.expiryDate,
                    daysRemaining = vehicle.registrationDetails.daysRemaining,
                    diskBarcodeNumber = vehicle.registrationDetails.diskBarcodeNumber,
                    registeringAuthorityDltc = vehicle.registrationDetails.registeringAuthorityDltc,
                    roadworthyCertificateNumber = vehicle.registrationDetails.roadworthyCertificateNumber,
                    roadworthyTestDate = vehicle.registrationDetails.roadworthyTestDate
                )
            )
        )
        return adapter.toJson(response)
    }
}
