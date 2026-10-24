package com.example.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit Service Interface for public vehicle database APIs.
 * Connects to public VIN decoding services (e.g. NHTSA VPIC international WMI/VIN decoder)
 * and South African NaTIS vehicle registration endpoints.
 */
interface VpicApiService {

    /**
     * Public VIN decoder endpoint providing live international vehicle specs,
     * manufacturer details, assembly plant locations, body types, and engine attributes.
     */
    @GET("vehicles/decodevinvalues/{vin}")
    suspend fun decodeVin(
        @Path("vin") vin: String,
        @Query("format") format: String = "json"
    ): VpicResponse
}

interface SaVehicleApiService {

    /**
     * Queries South African vehicle database for vehicle registration history,
     * NaTIS status, license disk validity, owner history, and SAPS clearance.
     */
    @GET("api/v1/natis/vehicle/{vin}")
    suspend fun getRegistrationHistory(
        @Path("vin") vin: String
    ): SaNatisVehicleResponse

    /**
     * Resolves South African license plate (e.g. GP, KZN, WC, EC) to
     * vehicle registration record and VIN.
     */
    @GET("api/v1/natis/plate/{plate}")
    suspend fun getVehicleByPlate(
        @Path("plate") plate: String
    ): SaNatisVehicleResponse
}
