package com.example

import com.example.data.VinDecoder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testVinCleaningAndValidation() {
        val validVin = "AHTBA3CD407129481"
        assertTrue(VinDecoder.isValidVin(validVin))
        assertEquals(17, VinDecoder.cleanVin(validVin).length)

        // Invalid length
        assertFalse(VinDecoder.isValidVin("AHTBA3CD407"))

        // Invalid letters I, O, Q
        assertFalse(VinDecoder.isValidVin("AHTBA3CD40712948I"))
        assertFalse(VinDecoder.isValidVin("AHTBA3CD40712948O"))
        assertFalse(VinDecoder.isValidVin("AHTBA3CD40712948Q"))
    }

    @Test
    fun testSouthAfricanWmiAndOwnerDetailsDecoding() {
        val hiluxVin = "AHTBA3CD407129481"
        val vehicle = VinDecoder.decode(hiluxVin)

        assertEquals("Toyota", vehicle.make)
        assertEquals("Hilux", vehicle.model)
        assertEquals("South Africa", vehicle.countryOfOrigin)
        assertTrue(vehicle.assemblyPlant.contains("Prospecton"))
        assertEquals(2024, vehicle.year)
        assertEquals("ACTIVE & LICENSED", vehicle.natisStatus)
        assertEquals("KwaZulu-Natal (KZN)", vehicle.province)
        assertFalse(vehicle.isStolen)
        assertTrue(vehicle.isFinanced)

        // Test Owner Details
        assertNotNull(vehicle.currentOwner)
        assertEquals("Justin", vehicle.currentOwner.firstName)
        assertEquals("Van Wyk", vehicle.currentOwner.surname)
        assertTrue(vehicle.currentOwner.streetAddress.contains("Umhlanga"))
        assertTrue(vehicle.currentOwner.city.contains("eThekwini"))

        // Test Ownership History
        assertTrue(vehicle.ownershipHistory.isNotEmpty())
        assertEquals(2, vehicle.ownershipHistory.size)
        assertEquals(1, vehicle.ownershipHistory[0].ownerNumber)

        // Test Accident & Insurance Record
        assertTrue(vehicle.accidentInsuranceRecord.hasClearHistory)
        assertFalse(vehicle.accidentInsuranceRecord.isFloodDamaged)
        assertFalse(vehicle.accidentInsuranceRecord.isInsuranceWriteOff)

        // Test Registration & License Disk Database
        assertEquals("VALID & ACTIVE", vehicle.registrationDetails.licenseDiskStatus)
        assertEquals("2026-11-30", vehicle.registrationDetails.expiryDate)
        assertTrue(vehicle.registrationDetails.daysRemaining > 0)
    }

    @Test
    fun testAccidentAndCode3RebuiltDetection() {
        val rebuiltVin = "ADM94038101783021"
        val vehicle = VinDecoder.decode(rebuiltVin)

        assertEquals("Isuzu", vehicle.make)
        assertEquals("D-Max", vehicle.model)
        assertFalse(vehicle.accidentInsuranceRecord.hasClearHistory)
        assertTrue(vehicle.accidentInsuranceRecord.isInsuranceWriteOff)
        assertTrue(vehicle.accidentInsuranceRecord.writeOffStatus.contains("Code 3"))
        assertEquals(1, vehicle.accidentInsuranceRecord.majorAccidentsCount)
        assertTrue(vehicle.accidentInsuranceRecord.claimsHistory.isNotEmpty())
        assertTrue(vehicle.accidentInsuranceRecord.claimsHistory[0].structuralFrameAffected)
    }

    @Test
    fun testSouthAfricanPlateLookup() {
        val hilux = VinDecoder.lookupByPlate("ND 842 190")
        assertEquals("Toyota", hilux.make)
        assertEquals("Hilux", hilux.model)
        assertEquals("AHTBA3CD407129481", hilux.vin)

        val polo = VinDecoder.lookupByPlate("CA 529 881")
        assertEquals("Volkswagen", polo.make)
        assertEquals("Polo Vivo", polo.model)
        assertEquals("AAVZZZ6RZMU048291", polo.vin)

        val ranger = VinDecoder.lookupByPlate("JH 21 XK GP")
        assertEquals("Ford", ranger.make)
        assertEquals("Ranger", ranger.model)
        assertEquals("AFAXXMRTXPK712039", ranger.vin)
    }

    @Test
    fun testDoubleVerificationEngine() {
        val hilux = VinDecoder.decode("AHTBA3CD407129481")
        val audit = com.example.data.verification.VehicleVerificationEngine.performDoubleVerification(hilux)

        assertTrue(audit.isOverallVerified)
        assertTrue(audit.verificationScore >= 80)
        assertEquals("Low Risk / Fully Verified", audit.fraudRiskLevel)
        assertEquals(8, audit.items.size)

        // Verify that SAPS check is verified and clear
        val sapsItem = audit.items.find { it.title.contains("SAPS") }
        assertNotNull(sapsItem)
        assertTrue(sapsItem!!.isVerified)

        // Verify that Plate item is verified
        val plateItem = audit.items.find { it.title.contains("Plate") }
        assertNotNull(plateItem)
        assertTrue(plateItem!!.isVerified)

        // Verify that Engine item is verified
        val engineItem = audit.items.find { it.title.contains("Engine") }
        assertNotNull(engineItem)
        assertTrue(engineItem!!.isVerified)
    }

    @Test
    fun testIso3779StandardValidation() {
        val validator = com.example.data.validation.Iso3779Validator

        // 1. Valid South African Toyota Hilux VIN
        val hiluxVin = "AHTBA3CD407129481"
        val hiluxResult = validator.validate(hiluxVin)
        assertTrue(hiluxResult.isValid)
        assertTrue(hiluxResult.lengthValid)
        assertTrue(hiluxResult.characterSetValid)
        assertTrue(hiluxResult.isSouthAfricanWmi)
        assertEquals("AHT", hiluxResult.wmi)
        assertEquals("South Africa", hiluxResult.wmiCountry)
        assertTrue(hiluxResult.manufacturerDescription.contains("Toyota"))
        assertEquals("BA3CD4", hiluxResult.vds)
        assertEquals('4', hiluxResult.checkDigit)
        assertEquals('0', hiluxResult.modelYearChar)
        assertTrue(hiluxResult.hasValidNumericEnding)

        // 2. Modulo 11 Check Digit calculation
        val checkDigit = validator.calculateCheckDigit(hiluxVin)
        assertNotNull(checkDigit)

        // 3. Prohibited Character 'I', 'O', 'Q' Detection (ISO 3779 Clause 4)
        val prohibitedVin = "AHTBA3CD40I129481"
        val prohibitedResult = validator.validate(prohibitedVin)
        assertFalse(prohibitedResult.isValid)
        assertFalse(prohibitedResult.characterSetValid)
        assertTrue(prohibitedResult.illegalCharacters.contains('I'))
        assertNotNull(prohibitedResult.errorReason)

        // 4. Invalid Length Detection
        val shortVin = "AHTBA3CD40712"
        val shortResult = validator.validate(shortVin)
        assertFalse(shortResult.isValid)
        assertFalse(shortResult.lengthValid)

        // 5. Valid VW Kariega Eastern Cape WMI
        val vwVin = "AAVZZZ6RZMU048291"
        val vwResult = validator.validate(vwVin)
        assertTrue(vwResult.isValid)
        assertTrue(vwResult.isSouthAfricanWmi)
        assertEquals("AAV", vwResult.wmi)
        assertTrue(vwResult.manufacturerDescription.contains("Volkswagen"))
    }
}

