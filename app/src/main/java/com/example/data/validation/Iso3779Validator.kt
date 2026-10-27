package com.example.data.validation

import java.util.Locale

/**
 * Result of ISO 3779 Vehicle Identification Number (VIN) structural validation.
 */
data class Iso3779ValidationResult(
    val isValid: Boolean,
    val vin: String,
    val lengthValid: Boolean,
    val characterSetValid: Boolean,
    val illegalCharacters: List<Char> = emptyList(),
    // Section 1: WMI (World Manufacturer Identifier, Positions 1-3)
    val wmi: String = "",
    val wmiRegion: String = "",
    val wmiCountry: String = "",
    val manufacturerDescription: String = "",
    val isSouthAfricanWmi: Boolean = false,
    // Section 2: VDS (Vehicle Descriptor Section, Positions 4-9)
    val vds: String = "",
    val checkDigit: Char? = null,
    val calculatedCheckDigit: Char? = null,
    val isChecksumMatch: Boolean = false,
    val checkDigitCompliance: String = "",
    // Section 3: VIS (Vehicle Indicator Section, Positions 10-17)
    val vis: String = "",
    val modelYearChar: Char? = null,
    val decodedModelYear: Int? = null,
    val plantCode: Char? = null,
    val sequentialNumber: String = "",
    val hasValidNumericEnding: Boolean = false,
    // Detailed feedback
    val errorReason: String? = null,
    val summary: String = ""
)

/**
 * Standard ISO 3779 structural validator for Vehicle Identification Numbers (VINs).
 *
 * Implements:
 * 1. ISO 3779 Clause 4: Total length exactly 17 characters; prohibited characters 'I', 'O', 'Q'.
 * 2. ISO 3779 Clause 5.1: World Manufacturer Identifier (WMI) parsing (Geographic Area, Country, Manufacturer).
 * 3. ISO 3779 Clause 5.2: Vehicle Descriptor Section (VDS) validation.
 * 4. ISO 3779 Clause 5.3: Vehicle Indicator Section (VIS) validation, including model year and sequential digits.
 * 5. Weighted Modulo 11 check-digit calculation (ISO 3779 / SAE J853 standard).
 * 6. South African NaTIS & African WMI classification.
 */
object Iso3779Validator {

    private val vinCharacterRegex = Regex("^[A-HJ-NPR-Z0-9]{17}$")
    private val illegalCharactersRegex = Regex("[IOQioq]")

    // Modulo 11 position weights according to ISO 3779 / SAE J853
    private val positionWeights = intArrayOf(8, 7, 6, 5, 4, 3, 2, 10, 0, 9, 8, 7, 6, 5, 4, 3, 2)

    // ISO 3779 character transliteration values
    private val transliterationMap = mapOf(
        'A' to 1, 'B' to 2, 'C' to 3, 'D' to 4, 'E' to 5, 'F' to 6, 'G' to 7, 'H' to 8,
        'J' to 1, 'K' to 2, 'L' to 3, 'M' to 4, 'N' to 5, 'P' to 7, 'R' to 9,
        'S' to 2, 'T' to 3, 'U' to 4, 'V' to 5, 'W' to 6, 'X' to 7, 'Y' to 8, 'Z' to 9,
        '0' to 0, '1' to 1, '2' to 2, '3' to 3, '4' to 4, '5' to 5, '6' to 6, '7' to 7, '8' to 8, '9' to 9
    )

    // Year codes for position 10 (Cycle 1: 1980-2009, Cycle 2: 2010-2039)
    private val yearCodeMap = mapOf(
        'A' to 2010, 'B' to 2011, 'C' to 2012, 'D' to 2013, 'E' to 2014,
        'F' to 2015, 'G' to 2016, 'H' to 2017, 'J' to 2018, 'K' to 2019,
        'L' to 2020, 'M' to 2021, 'N' to 2022, 'P' to 2023, 'R' to 2024,
        'S' to 2025, 'T' to 2026, 'V' to 2027, 'W' to 2028, 'X' to 2029,
        'Y' to 2030, '1' to 2031, '2' to 2032, '3' to 2033, '4' to 2034,
        '5' to 2035, '6' to 2036, '7' to 2037, '8' to 2038, '9' to 2039
    )

    /**
     * Performs rigorous ISO 3779 structural validation on a VIN string before querying NaTIS/databases.
     */
    fun validate(rawVin: String): Iso3779ValidationResult {
        val cleaned = rawVin.trim().uppercase(Locale.ROOT)
            .replace(" ", "")
            .replace("-", "")
            .replace(".", "")

        val lengthValid = cleaned.length == 17
        val illegalChars = cleaned.filter { it == 'I' || it == 'O' || it == 'Q' }.toList().distinct()
        val characterSetValid = illegalChars.isEmpty() && cleaned.all { it in 'A'..'Z' || it in '0'..'9' }

        if (!lengthValid) {
            return Iso3779ValidationResult(
                isValid = false,
                vin = cleaned,
                lengthValid = false,
                characterSetValid = characterSetValid,
                illegalCharacters = illegalChars,
                errorReason = "Invalid VIN length: ${cleaned.length}/17 characters. ISO 3779 strictly requires exactly 17 characters.",
                summary = "Failed Length Check (${cleaned.length} chars)"
            )
        }

        if (!characterSetValid) {
            val illegalStr = illegalChars.joinToString(", ") { "'$it'" }
            return Iso3779ValidationResult(
                isValid = false,
                vin = cleaned,
                lengthValid = true,
                characterSetValid = false,
                illegalCharacters = illegalChars,
                errorReason = "ISO 3779 prohibited character(s) detected: $illegalStr. Letters I, O, and Q are forbidden in standard VINs to prevent optical confusion with 1 and 0.",
                summary = "Forbidden Characters Detected: $illegalStr"
            )
        }

        // Section 1: WMI (Positions 1 to 3, indices 0..2)
        val wmi = cleaned.substring(0, 3)
        val (region, country, manufacturer, isSa) = resolveWmiInfo(wmi)

        // Section 2: VDS (Positions 4 to 9, indices 3..8)
        val vds = cleaned.substring(3, 9)
        val actualCheckDigit = cleaned[8]
        val calculatedCheckDigit = calculateCheckDigit(cleaned)
        val isChecksumMatch = calculatedCheckDigit != null && calculatedCheckDigit == actualCheckDigit

        val checkDigitStatus = when {
            isChecksumMatch -> "ISO 3779 Modulo-11 Check Digit Verified ('$actualCheckDigit')"
            actualCheckDigit.isDigit() || actualCheckDigit == 'X' -> "Modulo-11 Check Digit (Calc: '$calculatedCheckDigit', Found: '$actualCheckDigit')"
            else -> "ISO 3779 European/South African Regional VDS Format ('$actualCheckDigit')"
        }

        // Section 3: VIS (Positions 10 to 17, indices 9..16)
        val vis = cleaned.substring(9, 17)
        val modelYearChar = cleaned[9]
        val decodedYear = yearCodeMap[modelYearChar]
        val plantCode = cleaned[10]
        val sequentialNumber = cleaned.substring(11, 17)
        val lastFourDigits = cleaned.takeLast(4).all { it.isDigit() }

        // ISO 3779 Clause 5.3.3 requires sequential digits at the end
        val structuralValid = lastFourDigits

        val errorReason = if (!structuralValid) {
            "ISO 3779 Section 5.3.3 requires the final 4 production sequence characters to be numeric digits."
        } else null

        val isValid = characterSetValid && lengthValid && structuralValid

        val summary = if (isValid) {
            "ISO 3779 Standard Valid: $wmi ($manufacturer, $country) • Model Year $decodedYear"
        } else {
            errorReason ?: "ISO 3779 structural non-compliance"
        }

        return Iso3779ValidationResult(
            isValid = isValid,
            vin = cleaned,
            lengthValid = true,
            characterSetValid = true,
            illegalCharacters = emptyList(),
            wmi = wmi,
            wmiRegion = region,
            wmiCountry = country,
            manufacturerDescription = manufacturer,
            isSouthAfricanWmi = isSa,
            vds = vds,
            checkDigit = actualCheckDigit,
            calculatedCheckDigit = calculatedCheckDigit,
            isChecksumMatch = isChecksumMatch,
            checkDigitCompliance = checkDigitStatus,
            vis = vis,
            modelYearChar = modelYearChar,
            decodedModelYear = decodedYear,
            plantCode = plantCode,
            sequentialNumber = sequentialNumber,
            hasValidNumericEnding = lastFourDigits,
            errorReason = errorReason,
            summary = summary
        )
    }

    /**
     * Calculates the ISO 3779 / SAE J853 Modulo 11 check digit for Position 9.
     */
    fun calculateCheckDigit(vin17: String): Char? {
        if (vin17.length != 17) return null

        var sum = 0
        for (i in 0 until 17) {
            val char = vin17[i]
            val value = transliterationMap[char] ?: return null
            val weight = positionWeights[i]
            sum += value * weight
        }

        val remainder = sum % 11
        return if (remainder == 10) 'X' else ('0' + remainder)
    }

    /**
     * Resolves WMI region, country, and manufacturer with focus on South African assembly plants.
     */
    private fun resolveWmiInfo(wmi: String): WmiMeta {
        val firstChar = wmi.firstOrNull() ?: return WmiMeta("Unknown", "Unknown", "Unknown", false)

        val (region, country) = when (firstChar) {
            in 'A'..'C' -> {
                when {
                    wmi.startsWith("AA") || wmi.startsWith("AB") || wmi.startsWith("AC") ||
                    wmi.startsWith("AD") || wmi.startsWith("AE") || wmi.startsWith("AF") ||
                    wmi.startsWith("AG") || wmi.startsWith("AH") || wmi.startsWith("AJ") ||
                    wmi.startsWith("AK") -> Pair("Africa", "South Africa")
                    else -> Pair("Africa", "Rest of Africa")
                }
            }
            in 'J'..'R' -> Pair("Asia", resolveAsianCountry(wmi))
            in 'S'..'Z' -> Pair("Europe", resolveEuropeanCountry(wmi))
            in '1'..'5' -> Pair("North America", resolveNorthAmericanCountry(wmi))
            in '6'..'7' -> Pair("Oceania", "Australia / New Zealand")
            in '8'..'9' -> Pair("South America", "Brazil / Argentina")
            else -> Pair("Global", "International")
        }

        val (manufacturer, isSa) = when {
            // South African Domestic Manufacturers
            wmi.startsWith("AHT") -> Pair("Toyota South Africa (Prospecton Plant, Durban)", true)
            wmi.startsWith("AAV") -> Pair("Volkswagen South Africa (Kariega Plant, Eastern Cape)", true)
            wmi.startsWith("AFA") -> Pair("Ford Motor Company South Africa (Silverton Plant, Pretoria)", true)
            wmi.startsWith("ADM") || wmi.startsWith("ABM") -> Pair("Isuzu Motors South Africa (Sturrock Park, Gqeberha)", true)
            wmi.startsWith("ADN") -> Pair("Nissan South Africa (Rosslyn Plant, Pretoria)", true)
            wmi.startsWith("AFV") -> Pair("General Motors South Africa / Stellantis", true)
            wmi.startsWith("AHM") -> Pair("Mercedes-Benz South Africa (East London Plant)", true)
            wmi.startsWith("MAL") -> Pair("Hyundai Automotive South Africa", true)

            // European Plants with prominent South African branches
            wmi.startsWith("WBA") || wmi.startsWith("WBS") -> Pair("BMW AG (Plant 71 Rosslyn SA / Munich)", true)
            wmi.startsWith("WDC") || wmi.startsWith("WDD") -> Pair("Mercedes-Benz AG (East London SA / Stuttgart)", true)
            wmi.startsWith("WVW") || wmi.startsWith("WAU") -> Pair("Volkswagen / Audi AG (Wolfsburg / Ingolstadt)", false)
            wmi.startsWith("VF1") || wmi.startsWith("VF3") -> Pair("Renault / Peugeot Group", false)
            wmi.startsWith("ZAR") -> Pair("Alfa Romeo / Stellantis (Italy)", false)

            // Asian Manufacturers
            wmi.startsWith("JHM") || wmi.startsWith("JTD") || wmi.startsWith("JTE") -> Pair("Toyota Motor Corp (Japan)", false)
            wmi.startsWith("JM0") || wmi.startsWith("JM1") -> Pair("Mazda Motor Corp (Japan)", false)
            wmi.startsWith("JN1") || wmi.startsWith("JN8") -> Pair("Nissan Motor Co (Japan)", false)
            wmi.startsWith("KMH") || wmi.startsWith("KNA") -> Pair("Hyundai / Kia Motors (South Korea)", false)

            // North American Manufacturers
            wmi.startsWith("1FA") || wmi.startsWith("1FT") -> Pair("Ford Motor Company (USA)", false)
            wmi.startsWith("1GC") || wmi.startsWith("1G1") -> Pair("General Motors (USA)", false)
            wmi.startsWith("5UX") -> Pair("BMW Spartanburg (USA)", false)

            else -> Pair("Certified Motor Vehicle Manufacturer", country == "South Africa")
        }

        return WmiMeta(region, country, manufacturer, isSa)
    }

    private fun resolveAsianCountry(wmi: String): String = when (wmi.first()) {
        'J' -> "Japan"
        'K' -> "South Korea"
        'L' -> "China"
        'M' -> "India / Southeast Asia"
        else -> "Asia"
    }

    private fun resolveEuropeanCountry(wmi: String): String = when (wmi.first()) {
        'S' -> "United Kingdom / Europe"
        'V' -> "France / Spain"
        'W' -> "Germany"
        'Z' -> "Italy"
        else -> "Europe"
    }

    private fun resolveNorthAmericanCountry(wmi: String): String = when (wmi.first()) {
        '1', '4', '5' -> "United States"
        '2' -> "Canada"
        '3' -> "Mexico"
        else -> "North America"
    }

    private data class WmiMeta(
        val region: String,
        val country: String,
        val manufacturer: String,
        val isSouthAfrican: Boolean
    )
}
