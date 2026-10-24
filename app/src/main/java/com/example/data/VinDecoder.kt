package com.example.data

import com.example.data.model.AccidentInsuranceRecord
import com.example.data.model.InsuranceClaim
import com.example.data.model.OwnerDetails
import com.example.data.model.OwnerHistoryRecord
import com.example.data.model.RegistrationDetails
import com.example.data.model.VehicleDetails
import com.example.data.model.VinPreset
import java.util.Locale

object VinDecoder {

    val PRESETS = listOf(
        VinPreset(
            vin = "AHTBA3CD407129481",
            title = "Toyota Hilux 2.8 GD-6 4x4",
            subtitle = "2024 Legend RS Auto • Prospecton, KZN",
            province = "KwaZulu-Natal (KZN)"
        ),
        VinPreset(
            vin = "AAVZZZ6RZMU048291",
            title = "VW Polo Vivo 1.4 Comfortline",
            subtitle = "2021 Hatch 5-Door • Kariega, Eastern Cape",
            province = "Western Cape (WC)"
        ),
        VinPreset(
            vin = "AFAXXMRTXPK712039",
            title = "Ford Ranger 2.0 Bi-Turbo Wildtrak",
            subtitle = "2023 Double Cab 4x4 • Silverton, Pretoria",
            province = "Gauteng (GP)"
        ),
        VinPreset(
            vin = "WBA31AY00PFP18420",
            title = "BMW 320i M Sport (G20)",
            subtitle = "2022 Steptronic • Plant 71 Rosslyn, Pretoria",
            province = "Gauteng (GP)"
        ),
        VinPreset(
            vin = "ADM94038101783021",
            title = "Isuzu D-Max 250 (Code 3 Rebuilt Demo)",
            subtitle = "2019 Single Cab • Structural Repair Flag",
            province = "Eastern Cape (EC)"
        ),
        VinPreset(
            vin = "AHM2050402E192837",
            title = "Mercedes-Benz C200 AMG Line",
            subtitle = "2020 9G-Tronic • East London, Eastern Cape",
            province = "Western Cape (WC)"
        )
    )

    fun cleanVin(raw: String): String {
        return raw.trim().uppercase(Locale.ROOT).replace(Regex("[^A-Z0-9]"), "")
    }

    fun isValidVin(vin: String): Boolean {
        val clean = cleanVin(vin)
        if (clean.length != 17) return false
        // I, O, Q are not permitted in standard VINs
        if (clean.contains("I") || clean.contains("O") || clean.contains("Q")) return false
        return true
    }

    /**
     * Resolves a South African plate (e.g. ND 842 190, CA 529 881, JH 21 XK GP) to vehicle details
     */
    fun lookupByPlate(rawPlate: String): VehicleDetails {
        val clean = rawPlate.replace("%20", "").replace(" ", "").replace("-", "").uppercase(Locale.ROOT)
        return when {
            clean.contains("842190") || clean.contains("ND842") -> decode("AHTBA3CD407129481")
            clean.contains("529881") || clean.contains("CA529") -> decode("AAVZZZ6RZMU048291")
            clean.contains("21XK") || (clean.contains("JH21") && clean.contains("GP")) -> decode("AFAXXMRTXPK712039")
            clean.contains("88TW") || clean.contains("FC88") -> decode("WBA31AY00PFP18420")
            clean.contains("91KK") || clean.contains("DZ91") -> decode("ADM94038101783021")
            else -> {
                val hash = Math.abs(clean.hashCode())
                val prefix = if (clean.endsWith("GP")) "AFA" else if (clean.startsWith("CA") || clean.startsWith("CAA")) "AAV" else "AHT"
                val syntheticVin = "${prefix}ZZZ${(10000000000L + hash).toString().take(11)}"
                decode(syntheticVin)
            }
        }
    }

    fun decode(rawVin: String): VehicleDetails {
        val vin = cleanVin(rawVin).padEnd(17, '0').take(17)

        when (vin) {
            "AHTBA3CD407129481" -> return VehicleDetails(
                vin = vin,
                make = "Toyota",
                model = "Hilux",
                year = 2024,
                derivative = "2.8 GD-6 Raised Body Legend RS 4x4 Auto",
                bodyType = "Double Cab Bakkie (L.D.V)",
                fuelType = "Diesel (50ppm Turbocharged)",
                engineCapacityCc = "2755 cc",
                powerKw = "150 kW @ 3400 rpm / 500 Nm",
                transmission = "6-Speed Automatic",
                drivetrain = "Part-Time 4WD with Rear Diff Lock",
                color = "Glacier White",
                wmiCode = "AHT",
                countryOfOrigin = "South Africa",
                assemblyPlant = "Prospecton Assembly Plant (Durban, KZN)",
                natisStatus = "ACTIVE & LICENSED",
                registrationNumber = "ND 842 190",
                province = "KwaZulu-Natal (KZN)",
                licenseDiscExpiry = "2026-11-30",
                tareKg = 2100,
                gvmKg = 3210,
                engineNumber = "1GD-7492019",
                sapsStatus = "CLEAR - No SAPS Wanted or Stolen Circulation",
                isStolen = false,
                sapsClearanceRequired = false,
                isFinanced = true,
                titleholder = "WesBank (A Division of FirstRand Bank Ltd)",
                financeInstitution = "WesBank",
                settlementStatus = "Active Installment Agreement (Settlement Required)",
                isMicrodotVerified = true,
                microdotProvider = "DataDot South Africa (Cert: DD-ZA-99214)",
                accidentCode = "Code 2 (Standard Used Vehicle)",
                accidentSummary = "No structural accident records. Minor bumper cosmetic repaint logged in 2024.",
                airbagStatus = "Intact & Factory Active (All 7 Airbags Operational)",
                lastRecordedMileageKm = 34500,
                rollbackRisk = "Low (Consistent Franchise Service Records)",
                serviceHistory = "Full Toyota Franchise Service History (Prospecton Motors)",
                retailValueZar = 795000,
                tradeValueZar = 720000,
                privateValueZar = 755000,
                auctionValueZar = 675000,
                saRoadAdvisory = "High theft & hijacking risk in Gauteng/KZN. Top-tier telematics tracking unit (Cartrack / Tracker / Beame) mandatory for comprehensive insurance. Excellent resale liquidity across SA.",
                currentOwner = OwnerDetails(
                    firstName = "Justin",
                    surname = "Van Wyk",
                    streetAddress = "44 Umhlanga Rocks Drive",
                    suburb = "Durban North",
                    city = "eThekwini",
                    province = "KwaZulu-Natal",
                    postalCode = "4051",
                    maskedId = "940824*****088",
                    ownershipType = "Private Individual",
                    dateAcquired = "15 Jan 2024"
                ),
                ownershipHistory = listOf(
                    OwnerHistoryRecord(
                        ownerNumber = 1,
                        ownerName = "Toyota Financial Services SA (Fleet)",
                        ownershipType = "Corporate Executive Fleet",
                        location = "Prospecton, Durban",
                        periodYears = "2024 (6 months)",
                        startDate = "01 Jan 2024",
                        endDate = "14 Jan 2024",
                        transferReason = "Executive vehicle de-fleeted & sold via Toyota Approved Pre-Owned"
                    ),
                    OwnerHistoryRecord(
                        ownerNumber = 2,
                        ownerName = "Justin Van Wyk",
                        ownershipType = "Private Individual",
                        location = "Durban North, KZN",
                        periodYears = "2024 - Present",
                        startDate = "15 Jan 2024",
                        endDate = "Current",
                        transferReason = "Current Registered Owner of Record"
                    )
                ),
                accidentInsuranceRecord = AccidentInsuranceRecord(
                    hasClearHistory = true,
                    majorAccidentsCount = 0,
                    floodDamageStatus = "CLEARED - Zero Waterline / Submersion Record",
                    isFloodDamaged = false,
                    writeOffStatus = "None (Code 2 Clean Title)",
                    isInsuranceWriteOff = false,
                    claimsHistory = listOf(
                        InsuranceClaim(
                            claimId = "CLM-2024-KZN-0182",
                            date = "22 Apr 2024",
                            incidentType = "Parking Lot Cosmetic Bumper Scuff",
                            severity = "Minor",
                            repairCostZar = 7450,
                            sambraRepairer = "Renew-It Umhlanga (SAMBRA Major Structural Accredited)",
                            structuralFrameAffected = false
                        )
                    )
                ),
                registrationDetails = RegistrationDetails(
                    licenseDiskStatus = "VALID & ACTIVE",
                    expiryDate = "2026-11-30",
                    daysRemaining = 402,
                    diskBarcodeNumber = "MVLX-ZA-994182901-KZN",
                    registeringAuthorityDltc = "Durban Central DLTC (eThekwini Metropolitan Municipality)",
                    roadworthyCertificateNumber = "COR-KZN-883921",
                    roadworthyTestDate = "10 Jan 2024"
                ),
                aiInspectionReport = "NaTIS record verified at Durban Licensing Department. The Hilux Legend RS is in pristine mechanical state. Microdotting matches chassis and engine 1GD stamping. Note active WesBank finance interest requiring official settlement letter prior to private sale transfer."
            )

            "AAVZZZ6RZMU048291" -> return VehicleDetails(
                vin = vin,
                make = "Volkswagen",
                model = "Polo Vivo",
                year = 2021,
                derivative = "1.4 Comfortline 5-Door Hatch",
                bodyType = "Hatchback (5-Door)",
                fuelType = "Petrol (95 / 93 ULP)",
                engineCapacityCc = "1398 cc",
                powerKw = "63 kW @ 5000 rpm / 132 Nm",
                transmission = "5-Speed Manual",
                drivetrain = "Front Wheel Drive",
                color = "Flash Red",
                wmiCode = "AAV",
                countryOfOrigin = "South Africa",
                assemblyPlant = "Kariega (formerly Uitenhage), Eastern Cape",
                natisStatus = "ACTIVE & LICENSED",
                registrationNumber = "CA 529 881",
                province = "Western Cape (WC)",
                licenseDiscExpiry = "2026-08-31",
                tareKg = 1045,
                gvmKg = 1530,
                engineNumber = "CLP-4829103",
                sapsStatus = "CLEAR - Verified Clean on SAPS Stolen Database",
                isStolen = false,
                sapsClearanceRequired = false,
                isFinanced = false,
                titleholder = "Private Owner (Titleholder Document in Hand)",
                financeInstitution = "None (Paid Off)",
                settlementStatus = "Settled in Full (RC1 / Logbook Ready for Transfer)",
                isMicrodotVerified = true,
                microdotProvider = "VeriDot Technologies SA",
                accidentCode = "Code 2 (Standard Used Vehicle)",
                accidentSummary = "Clean title. Zero structural repair, flood or panel replacement found.",
                airbagStatus = "Intact & Factory Active (Dual Front Airbags)",
                lastRecordedMileageKm = 68200,
                rollbackRisk = "Low (Verified via RMI / VW Dealer Network)",
                serviceHistory = "Full Service History (Barons VW Cape Town)",
                retailValueZar = 189000,
                tradeValueZar = 162000,
                privateValueZar = 175000,
                auctionValueZar = 145000,
                saRoadAdvisory = "Extremely popular daily driver in South Africa with low cost of ownership. Ubiquitous spare parts availability. Susceptible to catalytic converter theft; park in secure garages.",
                currentOwner = OwnerDetails(
                    firstName = "Naledi",
                    surname = "Dlamini",
                    streetAddress = "12 Kloof Street",
                    suburb = "Gardens",
                    city = "Cape Town",
                    province = "Western Cape",
                    postalCode = "8001",
                    maskedId = "880512*****081",
                    ownershipType = "Private Individual",
                    dateAcquired = "10 Mar 2023"
                ),
                ownershipHistory = listOf(
                    OwnerHistoryRecord(
                        ownerNumber = 1,
                        ownerName = "Barons VW Cape Town (Dealer Demo)",
                        ownershipType = "Dealership Inventory",
                        location = "Foreshore, Cape Town",
                        periodYears = "2021 (8 months)",
                        startDate = "15 Feb 2021",
                        endDate = "20 Oct 2021",
                        transferReason = "First sale from franchise dealership to private buyer"
                    ),
                    OwnerHistoryRecord(
                        ownerNumber = 2,
                        ownerName = "Johan Van Der Merwe",
                        ownershipType = "Private Individual",
                        location = "Stellenbosch, Western Cape",
                        periodYears = "2021 - 2023",
                        startDate = "21 Oct 2021",
                        endDate = "08 Mar 2023",
                        transferReason = "Sold private sale upon relocation"
                    ),
                    OwnerHistoryRecord(
                        ownerNumber = 3,
                        ownerName = "Naledi Dlamini",
                        ownershipType = "Private Individual",
                        location = "Gardens, Cape Town",
                        periodYears = "2023 - Present",
                        startDate = "10 Mar 2023",
                        endDate = "Current",
                        transferReason = "Current Registered Owner of Record"
                    )
                ),
                accidentInsuranceRecord = AccidentInsuranceRecord(
                    hasClearHistory = true,
                    majorAccidentsCount = 0,
                    floodDamageStatus = "CLEARED - Zero Flood or Waterline Record",
                    isFloodDamaged = false,
                    writeOffStatus = "None (Code 2 Clean Title)",
                    isInsuranceWriteOff = false,
                    claimsHistory = emptyList()
                ),
                registrationDetails = RegistrationDetails(
                    licenseDiskStatus = "VALID & ACTIVE",
                    expiryDate = "2026-08-31",
                    daysRemaining = 311,
                    diskBarcodeNumber = "MVLX-ZA-182901928-WC",
                    registeringAuthorityDltc = "Cape Town Civic Centre DLTC (City of Cape Town)",
                    roadworthyCertificateNumber = "AVTS-WC-2023-77491",
                    roadworthyTestDate = "05 Mar 2023"
                ),
                aiInspectionReport = "NaTIS registration is in good standing with zero finance encumbrance. The vehicle has full Barons service stamps. Cambelt inspection due at 90,000 km. Ideal second-hand purchase with clean title."
            )

            "AFAXXMRTXPK712039" -> return VehicleDetails(
                vin = vin,
                make = "Ford",
                model = "Ranger",
                year = 2023,
                derivative = "2.0 SiT / Bi-Turbo Wildtrak 4x4 Double Cab",
                bodyType = "Double Cab Bakkie (L.D.V)",
                fuelType = "Diesel (50ppm Turbocharged)",
                engineCapacityCc = "1996 cc",
                powerKw = "154 kW @ 3750 rpm / 500 Nm",
                transmission = "10-Speed Electronic Auto",
                drivetrain = "e-4WD with Electronic Shift-On-The-Fly",
                color = "Cyber Orange Metallic",
                wmiCode = "AFA",
                countryOfOrigin = "South Africa",
                assemblyPlant = "Silverton Assembly Plant (Pretoria, Gauteng)",
                natisStatus = "ACTIVE & LICENSED",
                registrationNumber = "JH 21 XK GP",
                province = "Gauteng (GP)",
                licenseDiscExpiry = "2026-10-31",
                tareKg = 2240,
                gvmKg = 3280,
                engineNumber = "YN20-6819283",
                sapsStatus = "CLEAR - Police Stolen Circulation Clear",
                isStolen = false,
                sapsClearanceRequired = false,
                isFinanced = true,
                titleholder = "MFC, a division of Nedbank Ltd",
                financeInstitution = "MFC / Nedbank",
                settlementStatus = "Active Finance (Paid down 45%)",
                isMicrodotVerified = true,
                microdotProvider = "DataDot Technology SA",
                accidentCode = "Code 2 (Standard Used Vehicle)",
                accidentSummary = "Right rear tailgate scratch repaired under insurance in 2023. No structural weld marks.",
                airbagStatus = "Intact & Factory Active (7 Airbags)",
                lastRecordedMileageKm = 41200,
                rollbackRisk = "Low (FordPass Digital Service Records)",
                serviceHistory = "Ford Franchise Service Plan Active until 90,000 km",
                retailValueZar = 690000,
                tradeValueZar = 615000,
                privateValueZar = 650000,
                auctionValueZar = 570000,
                saRoadAdvisory = "Modern Next-Gen Ranger architecture built at Silverton plant. 10-speed transmission software update recommended at 45,000 km service. High demand vehicle in agricultural and executive fleet sectors.",
                currentOwner = OwnerDetails(
                    firstName = "Kobus",
                    surname = "Pretorius",
                    streetAddress = "78 Lynnwood Road",
                    suburb = "Brooklyn",
                    city = "Pretoria",
                    province = "Gauteng",
                    postalCode = "0181",
                    maskedId = "760319*****082",
                    ownershipType = "Private Individual",
                    dateAcquired = "12 Nov 2023"
                ),
                ownershipHistory = listOf(
                    OwnerHistoryRecord(
                        ownerNumber = 1,
                        ownerName = "Pretorius Engineering Projects (Pty) Ltd",
                        ownershipType = "Commercial / Company",
                        location = "Centurion, Gauteng",
                        periodYears = "2023 (11 months)",
                        startDate = "10 Jan 2023",
                        endDate = "11 Nov 2023",
                        transferReason = "Transferred from company asset ledger to director personal name"
                    ),
                    OwnerHistoryRecord(
                        ownerNumber = 2,
                        ownerName = "Kobus Pretorius",
                        ownershipType = "Private Individual",
                        location = "Brooklyn, Pretoria",
                        periodYears = "2023 - Present",
                        startDate = "12 Nov 2023",
                        endDate = "Current",
                        transferReason = "Current Registered Owner of Record"
                    )
                ),
                accidentInsuranceRecord = AccidentInsuranceRecord(
                    hasClearHistory = true,
                    majorAccidentsCount = 0,
                    floodDamageStatus = "CLEARED - No Flood Claims Logged",
                    isFloodDamaged = false,
                    writeOffStatus = "None (Code 2 Clean Title)",
                    isInsuranceWriteOff = false,
                    claimsHistory = listOf(
                        InsuranceClaim(
                            claimId = "CLM-2023-GP-9910",
                            date = "18 Sep 2023",
                            incidentType = "Tailgate dent & scratch from reversing into bollard",
                            severity = "Minor",
                            repairCostZar = 11200,
                            sambraRepairer = "Silverton Auto Craft (SAMBRA NSR Accredited)",
                            structuralFrameAffected = false
                        )
                    )
                ),
                registrationDetails = RegistrationDetails(
                    licenseDiskStatus = "VALID & ACTIVE",
                    expiryDate = "2026-10-31",
                    daysRemaining = 372,
                    diskBarcodeNumber = "MVLX-ZA-771920194-GP",
                    registeringAuthorityDltc = "Centurion DLTC (City of Tshwane)",
                    roadworthyCertificateNumber = "DEKRA-GP-2023-4109",
                    roadworthyTestDate = "05 Nov 2023"
                ),
                aiInspectionReport = "Produced locally at Silverton. NaTIS registration current with Pretoria registering authority. Engine YN20 and 10-speed transmission are in excellent condition. Ensure Nedbank settlement letter is obtained when closing deal."
            )

            "ADM94038101783021" -> return VehicleDetails(
                vin = vin,
                make = "Isuzu",
                model = "D-Max",
                year = 2019,
                derivative = "250 Fleetside Single Cab (Rebuilt)",
                bodyType = "Single Cab Bakkie (L.D.V)",
                fuelType = "Diesel (50ppm Turbocharged)",
                engineCapacityCc = "2499 cc",
                powerKw = "58 kW @ 3600 rpm / 176 Nm",
                transmission = "5-Speed Manual",
                drivetrain = "Rear Wheel Drive (4x2)",
                color = "Olympic White",
                wmiCode = "ADM",
                countryOfOrigin = "South Africa",
                assemblyPlant = "Struandale Assembly Plant (Gqeberha, Eastern Cape)",
                natisStatus = "ACTIVE (CODE 3 REBUILT REGISTERED)",
                registrationNumber = "DZ 91 KK EC",
                province = "Eastern Cape (EC)",
                licenseDiscExpiry = "2025-12-31",
                tareKg = 1680,
                gvmKg = 2850,
                engineNumber = "4JK1-881920",
                sapsStatus = "SAPS 263 CLEARANCE CERTIFICATE ON FILE",
                isStolen = false,
                sapsClearanceRequired = false,
                isFinanced = false,
                titleholder = "Private Registered Individual",
                financeInstitution = "None (Unencumbered / Cash)",
                settlementStatus = "Clear Title",
                isMicrodotVerified = true,
                microdotProvider = "DataDot South Africa (Re-microdotted after rebuild)",
                accidentCode = "Code 3 (Built-up / Structural Rebuilt)",
                accidentSummary = "CRITICAL: Declared Code 3 Structural Rebuilt. Heavy frontal collision in 2021 with chassis bench realignment.",
                airbagStatus = "Replaced & Re-certified during Code 3 Homologation",
                lastRecordedMileageKm = 148500,
                rollbackRisk = "Moderate (Discrepancy post-rebuild)",
                serviceHistory = "Independent RMI workshop logs post-2021",
                retailValueZar = 145000,
                tradeValueZar = 110000,
                privateValueZar = 125000,
                auctionValueZar = 90000,
                saRoadAdvisory = "WARNING: Code 3 vehicles trade at a 30-40% discount in South Africa. Many major insurers (Discovery/Santam) offer third-party only cover for Code 3 units. Mandatory chassis engineer report recommended.",
                currentOwner = OwnerDetails(
                    firstName = "Thabo",
                    surname = "Mokoena",
                    streetAddress = "15 Commercial Road",
                    suburb = "Sidwell",
                    city = "Gqeberha",
                    province = "Eastern Cape",
                    postalCode = "6001",
                    maskedId = "820914*****086",
                    ownershipType = "Private Individual",
                    dateAcquired = "20 Feb 2022"
                ),
                ownershipHistory = listOf(
                    OwnerHistoryRecord(
                        ownerNumber = 1,
                        ownerName = "Eastern Cape Logistics Services",
                        ownershipType = "Commercial Fleet",
                        location = "East London, Eastern Cape",
                        periodYears = "2019 - 2021",
                        startDate = "12 Apr 2019",
                        endDate = "14 Aug 2021",
                        transferReason = "Written off after major collision on N2 highway"
                    ),
                    OwnerHistoryRecord(
                        ownerNumber = 2,
                        ownerName = "Algoa Salvage & Rebuilders CC",
                        ownershipType = "Licensed Rebuilder",
                        location = "Struandale, Gqeberha",
                        periodYears = "2021 - 2022",
                        startDate = "15 Aug 2021",
                        endDate = "18 Feb 2022",
                        transferReason = "Rebuilt, chassis straightened, SAPS 263 and Roadworthy passed"
                    ),
                    OwnerHistoryRecord(
                        ownerNumber = 3,
                        ownerName = "Thabo Mokoena",
                        ownershipType = "Private Individual",
                        location = "Sidwell, Gqeberha",
                        periodYears = "2022 - Present",
                        startDate = "20 Feb 2022",
                        endDate = "Current",
                        transferReason = "Current Registered Owner of Record"
                    )
                ),
                accidentInsuranceRecord = AccidentInsuranceRecord(
                    hasClearHistory = false,
                    majorAccidentsCount = 1,
                    floodDamageStatus = "CLEARED - Zero Waterline / Submersion Record",
                    isFloodDamaged = false,
                    writeOffStatus = "Code 3 Structural Rebuilt (Total Loss Write-off in 2021)",
                    isInsuranceWriteOff = true,
                    claimsHistory = listOf(
                        InsuranceClaim(
                            claimId = "CLM-2021-EC-TOTAL-0091",
                            date = "12 Aug 2021",
                            incidentType = "Major Head-On Collision (N2 Highway)",
                            severity = "Major / Structural",
                            repairCostZar = 142000,
                            sambraRepairer = "Algoa Certified Structural Rebuild Centre",
                            structuralFrameAffected = true
                        )
                    )
                ),
                registrationDetails = RegistrationDetails(
                    licenseDiskStatus = "DUE FOR RENEWAL (Within 30 Days)",
                    expiryDate = "2025-12-31",
                    daysRemaining = 24,
                    diskBarcodeNumber = "MVLX-ZA-448102910-EC",
                    registeringAuthorityDltc = "Gqeberha DLTC (Nelson Mandela Bay Municipality)",
                    roadworthyCertificateNumber = "SABS-EC-2022-99014",
                    roadworthyTestDate = "15 Feb 2022"
                ),
                aiInspectionReport = "ALERT: Vehicle is officially registered as Code 3 on NaTIS. While it possesses valid SAPS 263 clearance and roadworthy inspection, structural crash history on the N2 is on record. Ensure insurance underwriting terms are pre-approved before purchase."
            )
        }

        // Generic decoding for any 17-digit South African VIN entered by user
        val wmi = vin.take(3)
        val yearChar = vin.getOrNull(9) ?: 'R'
        val plantChar = vin.getOrNull(10) ?: 'U'

        val year = decodeModelYear(yearChar)
        val (make, country, plant) = decodeWmiAndPlant(wmi, plantChar)
        val model = inferModel(make, vin)

        val hash = Math.abs(vin.hashCode())
        val randomMileage = 25000 + (hash % 110000)
        val retailPrice = 180000 + (hash % 650000)
        val tradePrice = (retailPrice * 0.86).toInt()
        val privatePrice = (retailPrice * 0.93).toInt()
        val auctionPrice = (retailPrice * 0.77).toInt()

        val isSaBuild = wmi.startsWith("AA") || wmi.startsWith("AB") || wmi.startsWith("AC") ||
                wmi.startsWith("AD") || wmi.startsWith("AF") || wmi.startsWith("AH")

        val province = when (hash % 4) {
            0 -> "Gauteng (GP)"
            1 -> "Western Cape (WC)"
            2 -> "KwaZulu-Natal (KZN)"
            else -> "Eastern Cape (EC)"
        }

        val regPlate = when (province) {
            "Gauteng (GP)" -> "KP ${10 + (hash % 89)} YR GP"
            "Western Cape (WC)" -> "CAA ${100 + (hash % 899)}"
            "KwaZulu-Natal (KZN)" -> "ND ${200 + (hash % 799)} 81"
            else -> "DZ ${10 + (hash % 89)} EC"
        }

        val suburbs = when (province) {
            "Gauteng (GP)" -> listOf("Sandton" to "Johannesburg", "Brooklyn" to "Pretoria", "Fourways" to "Johannesburg")
            "Western Cape (WC)" -> listOf("Camps Bay" to "Cape Town", "Durbanville" to "Cape Town", "Stellenbosch" to "Stellenbosch")
            "KwaZulu-Natal (KZN)" -> listOf("Umhlanga" to "Durban", "Ballito" to "Dolphin Coast", "Morningside" to "Durban")
            else -> listOf("Walmer" to "Gqeberha", "Nahoon" to "East London", "Mill Park" to "Gqeberha")
        }
        val (suburb, city) = suburbs[hash % suburbs.size]

        val firstNames = listOf("Johan", "Sipho", "Pieter", "Bongani", "Francois", "Lwazi", "David", "Khanyi", "Mark", "Thabo")
        val surnames = listOf("Van Der Merwe", "Khumalo", "Botha", "Ndlovu", "Pretorius", "Dlamini", "Smith", "Nkosi", "Van Zyl", "Mokoena")
        val ownerFirst = firstNames[hash % firstNames.size]
        val ownerLast = surnames[(hash / 2) % surnames.size]

        val hasPriorClaims = (hash % 3 == 0)
        val hasClearAccident = !hasPriorClaims

        val daysLeft = 15 + (hash % 340)
        val diskStatus = if (daysLeft < 30) "DUE FOR RENEWAL (Within 30 Days)" else "VALID & ACTIVE"

        return VehicleDetails(
            vin = vin,
            make = make,
            model = model,
            year = year,
            derivative = "$make $model (South Africa Spec)",
            bodyType = if (make.contains("Toyota") || make.contains("Ford") || make.contains("Isuzu")) "Double Cab / Bakkie" else "Sedan / Hatch",
            fuelType = if (make.contains("Toyota") || make.contains("Isuzu") || make.contains("Ford")) "Diesel (50ppm Turbocharged)" else "Petrol (95 ULP)",
            engineCapacityCc = "${1400 + (hash % 16) * 100} cc",
            powerKw = "${75 + (hash % 110)} kW",
            transmission = if (hash % 2 == 0) "Automatic" else "6-Speed Manual",
            drivetrain = if (make.contains("Toyota") || make.contains("Ford")) "4x4 / All-Wheel Drive" else "Front-Wheel Drive",
            color = listOf("Pure White", "Shadow Silver", "Deep Impact Blue", "Graphite Grey", "Crimson Red")[hash % 5],
            wmiCode = wmi,
            countryOfOrigin = country,
            assemblyPlant = plant,
            natisStatus = "ACTIVE & LICENSED",
            registrationNumber = regPlate,
            province = province,
            licenseDiscExpiry = "2026-${String.format("%02d", 1 + hash % 12)}-28",
            tareKg = 1200 + (hash % 900),
            gvmKg = 1800 + (hash % 1300),
            engineNumber = "ENG-${hash % 9999999}",
            sapsStatus = "CLEAR - No SAPS Wanted Records",
            isStolen = false,
            sapsClearanceRequired = false,
            isFinanced = hash % 3 != 0,
            titleholder = if (hash % 3 != 0) "Standard Bank Vehicle & Asset Finance" else "Private Registered Owner",
            financeInstitution = if (hash % 3 != 0) "Standard Bank VAF" else "None (Clear Title)",
            settlementStatus = if (hash % 3 != 0) "Encumbered (Settlement Required)" else "Clear Title",
            isMicrodotVerified = true,
            microdotProvider = "DataDot South Africa (Regulation 56)",
            accidentCode = "Code 2 (Standard Used Vehicle)",
            accidentSummary = if (hasClearAccident) "Clean Title. Zero structural accident or write-off history found." else "Minor cosmetic fender claim recorded in 2023. Chassis structure intact.",
            airbagStatus = "Intact & Certified Functional",
            lastRecordedMileageKm = randomMileage,
            rollbackRisk = "Low Risk (Regular roadworthy inspection logs)",
            serviceHistory = "Franchise & RMI Approved Service Logs",
            retailValueZar = retailPrice,
            tradeValueZar = tradePrice,
            privateValueZar = privatePrice,
            auctionValueZar = auctionPrice,
            saRoadAdvisory = if (isSaBuild) "Manufactured locally in South Africa. Abundant spare parts and aftermarket dealer support across all 9 provinces." else "Imported CBU vehicle. Ensure parts availability through authorized dealer channels.",
            currentOwner = OwnerDetails(
                firstName = ownerFirst,
                surname = ownerLast,
                streetAddress = "${10 + (hash % 180)} Main Road",
                suburb = suburb,
                city = city,
                province = province,
                postalCode = "${2000 + (hash % 7000)}",
                maskedId = "${70 + (hash % 30)}${String.format("%02d", 1 + hash % 12)}${String.format("%02d", 1 + hash % 28)}*****08${hash % 9}",
                ownershipType = "Private Individual",
                dateAcquired = "14 Feb ${year + 1}"
            ),
            ownershipHistory = listOf(
                OwnerHistoryRecord(
                    ownerNumber = 1,
                    ownerName = "$make South Africa Dealership Demo",
                    ownershipType = "Dealership Fleet",
                    location = "$suburb, $province",
                    periodYears = "$year (1 year)",
                    startDate = "10 Jan $year",
                    endDate = "12 Feb ${year + 1}",
                    transferReason = "Sold as Certified Pre-Owned to second owner"
                ),
                OwnerHistoryRecord(
                    ownerNumber = 2,
                    ownerName = "$ownerFirst $ownerLast",
                    ownershipType = "Private Individual",
                    location = "$city, $province",
                    periodYears = "${year + 1} - Present",
                    startDate = "14 Feb ${year + 1}",
                    endDate = "Current",
                    transferReason = "Current Registered Owner of Record"
                )
            ),
            accidentInsuranceRecord = AccidentInsuranceRecord(
                hasClearHistory = hasClearAccident,
                majorAccidentsCount = if (hasClearAccident) 0 else 1,
                floodDamageStatus = "CLEARED - Zero Waterline Submersion Logged",
                isFloodDamaged = false,
                writeOffStatus = "None (Code 2 Clean Title)",
                isInsuranceWriteOff = false,
                claimsHistory = if (hasClearAccident) emptyList() else listOf(
                    InsuranceClaim(
                        claimId = "CLM-${hash % 99999}",
                        date = "14 May 2023",
                        incidentType = "Cosmetic Left Front Wing & Headlamp Scrape",
                        severity = "Minor",
                        repairCostZar = 9800,
                        sambraRepairer = "Certified RMI / SAMBRA Repair Workshop",
                        structuralFrameAffected = false
                    )
                )
            ),
            registrationDetails = RegistrationDetails(
                licenseDiskStatus = diskStatus,
                expiryDate = "2026-${String.format("%02d", 1 + hash % 12)}-28",
                daysRemaining = daysLeft,
                diskBarcodeNumber = "MVLX-ZA-${hash % 999999999}-${province.take(2)}",
                registeringAuthorityDltc = "$city Central DLTC",
                roadworthyCertificateNumber = "COR-${hash % 899999}",
                roadworthyTestDate = "12 Feb ${year + 1}"
            ),
            aiInspectionReport = "South African NaTIS records show active registration in $province. The VIN check confirms matching engine and chassis serials. No SAPS stolen interest or accident write-off flags detected."
        )
    }

    private fun decodeModelYear(yearCode: Char): Int {
        val mapping = mapOf(
            'A' to 2010, 'B' to 2011, 'C' to 2012, 'D' to 2013, 'E' to 2014,
            'F' to 2015, 'G' to 2016, 'H' to 2017, 'J' to 2018, 'K' to 2019,
            'L' to 2020, 'M' to 2021, 'N' to 2022, 'P' to 2023, 'R' to 2024,
            'S' to 2025, 'T' to 2026, 'V' to 2027, 'W' to 2028, 'X' to 2029,
            'Y' to 2030, '1' to 2001, '2' to 2002, '3' to 2003, '4' to 2004,
            '5' to 2005, '6' to 2006, '7' to 2007, '8' to 2008, '9' to 2009
        )
        return mapping[yearCode] ?: 2023
    }

    private fun decodeWmiAndPlant(wmi: String, plantChar: Char): Triple<String, String, String> {
        return when (wmi) {
            "AHT" -> Triple("Toyota", "South Africa", "Prospecton Plant (Durban, KZN)")
            "AAV" -> Triple("Volkswagen", "South Africa", "Kariega / Uitenhage Plant (Eastern Cape)")
            "AFA" -> Triple("Ford", "South Africa", "Silverton Plant (Pretoria, Gauteng)")
            "AHM" -> Triple("Mercedes-Benz", "South Africa", "East London Plant (Eastern Cape)")
            "ADM" -> Triple("Isuzu", "South Africa", "Struandale Plant (Gqeberha, EC)")
            "WBA", "WBS" -> Triple("BMW", "South Africa / Germany", if (plantChar == 'P' || plantChar == '7') "Plant 71 Rosslyn (Pretoria)" else "Munich / Dingolfing Germany")
            "WAU" -> Triple("Audi", "Germany", "Ingolstadt / Neckarsulm")
            "1HG", "2HG", "JH4" -> Triple("Honda", "USA / Japan", "Suzuka / Marysville")
            "JN1", "JN6", "JN8" -> Triple("Nissan", "Japan / South Africa", "Rosslyn Pretoria / Tochigi")
            "VF1" -> Triple("Renault", "France", "Douai / Flins")
            "KMH", "TMA" -> Triple("Hyundai", "South Korea / Czech", "Ulsan Plant")
            "KNA" -> Triple("Kia", "South Korea", "Hwaseong Plant")
            "SAL" -> Triple("Land Rover", "United Kingdom", "Solihull Plant")
            else -> {
                if (wmi.startsWith("A")) {
                    Triple("South Africa Manufacturer ($wmi)", "South Africa", "South African Assembly Plant")
                } else {
                    Triple("Global Manufacturer ($wmi)", "International Import", "Overseas Assembly Plant")
                }
            }
        }
    }

    private fun inferModel(make: String, vin: String): String {
        return when {
            make.contains("Toyota", ignoreCase = true) -> if (vin.contains("BA") || vin.contains("3C")) "Hilux" else if (vin.contains("COR")) "Corolla Cross" else "Fortuner"
            make.contains("Volkswagen", ignoreCase = true) -> if (vin.contains("6R")) "Polo Vivo" else if (vin.contains("2K")) "Caddy" else "Polo TSI"
            make.contains("Ford", ignoreCase = true) -> if (vin.contains("RT") || vin.contains("MR")) "Ranger" else "Everest"
            make.contains("BMW", ignoreCase = true) -> "3 Series (G20)"
            make.contains("Mercedes", ignoreCase = true) -> "C-Class"
            make.contains("Isuzu", ignoreCase = true) -> "D-Max"
            else -> "Commercial / Passenger Model"
        }
    }
}
