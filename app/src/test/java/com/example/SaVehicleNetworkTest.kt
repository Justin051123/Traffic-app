package com.example

import com.example.data.network.ApiClient
import com.example.data.network.SaNatisVehicleResponse
import com.example.data.network.SaVehicleRepository
import com.example.data.network.VpicResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SaVehicleNetworkTest {

    @Test
    fun testMoshiVpicResponseDeserialization() {
        val sampleVpicJson = """
            {
                "Count": 1,
                "Message": "Results returned successfully",
                "Results": [
                    {
                        "VIN": "AHTBA3CD407129481",
                        "Make": "TOYOTA",
                        "Model": "Hilux",
                        "ModelYear": "2024",
                        "Manufacturer": "TOYOTA MOTOR CORPORATION",
                        "PlantCountry": "SOUTH AFRICA",
                        "PlantCity": "DURBAN",
                        "VehicleType": "TRUCK",
                        "BodyClass": "Pickup",
                        "DisplacementL": "2.8",
                        "EngineCylinders": "4"
                    }
                ]
            }
        """.trimIndent()

        val adapter = ApiClient.moshi.adapter(VpicResponse::class.java)
        val response = adapter.fromJson(sampleVpicJson)

        assertNotNull(response)
        assertEquals(1, response?.count)
        val result = response?.results?.firstOrNull()
        assertNotNull(result)
        assertEquals("TOYOTA", result?.make)
        assertEquals("Hilux", result?.model)
        assertEquals("SOUTH AFRICA", result?.plantCountry)
        assertEquals("2.8", result?.displacementL)
    }

    @Test
    fun testMoshiSaNatisResponseDeserialization() {
        val sampleNatisJson = """
            {
                "statusCode": 200,
                "statusMessage": "SUCCESS",
                "dataSource": "NaTIS South Africa Live Gateway",
                "data": {
                    "vin": "AHTBA3CD407129481",
                    "registrationNumber": "ND 842 190",
                    "make": "Toyota",
                    "model": "Hilux",
                    "year": 2024,
                    "derivative": "2.8 GD-6 Legend RS 4x4",
                    "natisStatus": "ACTIVE & LICENSED",
                    "province": "KwaZulu-Natal (KZN)",
                    "licenseDiscExpiry": "2026-11-30",
                    "isStolen": false,
                    "isFinanced": true,
                    "accidentCode": "Code 2 (Clean / Used)",
                    "currentOwner": {
                        "firstName": "Justin",
                        "surname": "Van Wyk",
                        "streetAddress": "44 Lighthouse Road",
                        "suburb": "Umhlanga Rocks",
                        "city": "Durban / eThekwini",
                        "province": "KwaZulu-Natal (KZN)",
                        "postalCode": "4320",
                        "maskedId": "850412•••••••",
                        "ownershipType": "Private Individual",
                        "dateAcquired": "2024-02-10"
                    },
                    "registrationDetails": {
                        "licenseDiskStatus": "VALID & ACTIVE",
                        "expiryDate": "2026-11-30",
                        "daysRemaining": 242,
                        "diskBarcodeNumber": "MVLX-ND842190-2026",
                        "registeringAuthorityDltc": "Rossburgh DLTC, Durban (KZN)",
                        "roadworthyCertificateNumber": "RWC-2024-88491",
                        "roadworthyTestDate": "2024-02-05"
                    }
                }
            }
        """.trimIndent()

        val adapter = ApiClient.moshi.adapter(SaNatisVehicleResponse::class.java)
        val response = adapter.fromJson(sampleNatisJson)

        assertNotNull(response)
        assertEquals(200, response?.statusCode)
        val data = response?.data
        assertNotNull(data)
        assertEquals("Toyota", data?.make)
        assertEquals("Hilux", data?.model)
        assertEquals("ND 842 190", data?.registrationNumber)
        assertEquals("Justin", data?.currentOwner?.firstName)
        assertEquals("VALID & ACTIVE", data?.registrationDetails?.licenseDiskStatus)
    }

    @Test
    fun testRepositoryFetchVehicleRegistration() = runTest {
        val repository = SaVehicleRepository()
        val result = repository.fetchVehicleRegistration("AHTBA3CD407129481")

        assertTrue(result.isSuccess)
        val reg = result.getOrNull()
        assertNotNull(reg)

        val vehicle = reg!!.vehicleDetails
        assertEquals("Toyota", vehicle.make)
        assertEquals("Hilux", vehicle.model)
        assertEquals(2024, vehicle.year)
        assertEquals("ND 842 190", vehicle.registrationNumber)
        assertEquals("ACTIVE & LICENSED", vehicle.natisStatus)
        assertEquals("VALID & ACTIVE", vehicle.registrationDetails.licenseDiskStatus)
        assertFalse(vehicle.isStolen)
        assertTrue(vehicle.accidentInsuranceRecord.hasClearHistory)
        assertNotNull(vehicle.currentOwner)
        assertTrue(reg.sourceDescription.contains("NaTIS"))
    }

    @Test
    fun testRepositoryFetchVehicleByPlate() = runTest {
        val repository = SaVehicleRepository()

        // Lookup KZN plate
        val kznResult = repository.fetchVehicleByPlate("ND 842 190")
        assertTrue(kznResult.isSuccess)
        val kznCar = kznResult.getOrNull()!!.vehicleDetails
        assertEquals("Toyota", kznCar.make)
        assertEquals("Hilux", kznCar.model)
        assertEquals("AHTBA3CD407129481", kznCar.vin)

        // Lookup Western Cape plate
        val wcResult = repository.fetchVehicleByPlate("CA 529 881")
        assertTrue(wcResult.isSuccess)
        val wcCar = wcResult.getOrNull()!!.vehicleDetails
        assertEquals("Volkswagen", wcCar.make)
        assertEquals("Polo Vivo", wcCar.model)
        assertEquals("AAVZZZ6RZMU048291", wcCar.vin)

        // Lookup Gauteng plate
        val gpResult = repository.fetchVehicleByPlate("JH 21 XK GP")
        assertTrue(gpResult.isSuccess)
        val gpCar = gpResult.getOrNull()!!.vehicleDetails
        assertEquals("Ford", gpCar.make)
        assertEquals("Ranger", gpCar.model)
    }
}
