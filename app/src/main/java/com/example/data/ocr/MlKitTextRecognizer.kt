package com.example.data.ocr

import android.graphics.Bitmap
import com.example.data.VinDecoder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class OcrScanResult(
    val detectedVin: String? = null,
    val detectedPlate: String? = null,
    val detectedLicenseDiscExpiry: String? = null,
    val detectedMake: String? = null,
    val detectedSeries: String? = null,
    val detectedEngineNumber: String? = null,
    val detectedRegisterNumber: String? = null,
    val rawExtractedLines: List<String> = emptyList(),
    val scanSource: String = "Auto Detect",
    val confidenceSummary: String = "Normal"
)

object MlKitTextRecognizer {

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    // Standard 17-character VIN regex: strictly ISO 3779 compliant (letters A-Z excluding I, O, Q)
    private val standardVinRegex = Regex("[A-HJ-NPR-Z0-9]{17}")

    // Stamped VIN candidate regex allowing characters commonly confused by OCR on etched metal (I, O, Q)
    private val candidateVinRegex = Regex("[A-Z0-9]{17}")

    // South African Provincial and standard plate regex patterns
    // e.g.: "ND 842 190", "CA 782-990", "JH 21 XK GP", "BJW 209 EC", "DZ 91 KK EC", "FC 88 TW GP"
    private val plateRegexList = listOf(
        Regex("[A-Z]{2}\\s*\\d{2}\\s*[A-Z]{2}\\s*GP", RegexOption.IGNORE_CASE), // Gauteng (GP)
        Regex("[A-Z]{1,3}\\s*\\d{3,6}\\s*(GP|ZN|NC|EC|NW|MP|L|FS)?", RegexOption.IGNORE_CASE),
        Regex("(CA|CF|CY|CK|CJ|CW|CL|CAM|CEO|CFR|CCC)\\s*\\d{3,6}", RegexOption.IGNORE_CASE), // Western Cape (WC)
        Regex("(ND|ZN|NU|NPN|NP|NRB|NKR)\\s*\\d{3,6}", RegexOption.IGNORE_CASE), // KwaZulu-Natal (KZN)
        Regex("[A-Z]{2,3}\\s*\\d{3}[-\\s]*\\d{3}", RegexOption.IGNORE_CASE),
        Regex("[A-Z]{2}\\s*\\d{2}\\s*[A-Z]{2}", RegexOption.IGNORE_CASE)
    )

    // License disc expiry date patterns: e.g. "2025-08-31" or "31/08/2025" or "2026-11-30"
    private val expiryRegex = Regex("(202[4-9]|203[0-5])[-\\/](0[1-9]|1[0-2])[-\\/](0[1-9]|[12][0-9]|3[01])")
    private val altExpiryRegex = Regex("(0[1-9]|[12][0-9]|3[01])[-\\/](0[1-9]|1[0-2])[-\\/](202[4-9]|203[0-5])")

    /**
     * Specialized scanner for vehicle Dashboard VIN plates (lower driver-side corner of windshield,
     * door pillar jamb, or engine firewall stamping) using Google ML Kit on-device Text Recognition.
     */
    suspend fun scanDashboardVin(bitmap: Bitmap): Result<OcrScanResult> =
        suspendCancellableCoroutine { continuation ->
            try {
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText: Text ->
                        val result = parseDashboardVin(visionText)
                        continuation.resume(Result.success(result))
                    }
                    .addOnFailureListener { error ->
                        continuation.resume(Result.failure(error))
                    }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }

    /**
     * Specialized scanner for South African circular MVLX Vehicle License Discs
     * using Google ML Kit on-device Text Recognition.
     */
    suspend fun scanLicenseDisc(bitmap: Bitmap): Result<OcrScanResult> =
        suspendCancellableCoroutine { continuation ->
            try {
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText: Text ->
                        val result = parseLicenseDisc(visionText)
                        continuation.resume(Result.success(result))
                    }
                    .addOnFailureListener { error ->
                        continuation.resume(Result.failure(error))
                    }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }

    /**
     * General image text processor (falls back across Dashboard VIN, License Disc, and Number Plate).
     */
    suspend fun processImage(bitmap: Bitmap): Result<OcrScanResult> =
        suspendCancellableCoroutine { continuation ->
            try {
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText: Text ->
                        // First attempt dashboard VIN extraction, then license disc extraction
                        val dashResult = parseDashboardVin(visionText)
                        if (dashResult.detectedVin != null && dashResult.detectedVin.length == 17) {
                            continuation.resume(Result.success(dashResult))
                            return@addOnSuccessListener
                        }
                        val discResult = parseLicenseDisc(visionText)
                        if (discResult.detectedVin != null || discResult.detectedPlate != null) {
                            continuation.resume(Result.success(discResult))
                            return@addOnSuccessListener
                        }
                        // Default general parser
                        continuation.resume(Result.success(dashResult.copy(scanSource = "General OCR")))
                    }
                    .addOnFailureListener { error ->
                        continuation.resume(Result.failure(error))
                    }
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }

    /**
     * Extracts 17-character VIN from a dashboard or windshield VIN plate image.
     */
    private fun parseDashboardVin(visionText: Text): OcrScanResult {
        val allLines = mutableListOf<String>()
        var foundVin: String? = null
        var foundMake: String? = null
        val fullText = visionText.text

        // Scan line-by-line first
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val text = line.text.trim()
                allLines.add(text)

                // 1. Direct standard 17-char VIN
                if (foundVin == null) {
                    val candidate = normalizeVinCandidate(text)
                    val directMatch = standardVinRegex.find(candidate)
                    if (directMatch != null && VinDecoder.isValidVin(directMatch.value)) {
                        foundVin = directMatch.value
                    }
                }

                // 2. Look for lines with VIN keyword labels ("VIN:", "CHASSIS:", "V.I.N", "ONDERSTEL")
                if (foundVin == null) {
                    val upper = text.uppercase()
                    if (upper.contains("VIN") || upper.contains("CHASSIS") || upper.contains("ONDERSTEL") || upper.contains("WMI")) {
                        val cleanedAfterLabel = upper
                            .substringAfter("VIN")
                            .substringAfter("CHASSIS")
                            .substringAfter("ONDERSTEL")
                            .replace(":", "")
                            .replace("NO", "")
                            .replace(".", "")
                            .trim()

                        val normalized = normalizeVinCandidate(cleanedAfterLabel)
                        val match = standardVinRegex.find(normalized)
                        if (match != null && VinDecoder.isValidVin(match.value)) {
                            foundVin = match.value
                        }
                    }
                }

                // Detect Make from logo/text
                if (foundMake == null) {
                    foundMake = detectVehicleMake(text)
                }
            }
        }

        // Consolidated text scan if not found in single lines
        if (foundVin == null) {
            val consolidated = normalizeVinCandidate(fullText)
            val match = standardVinRegex.find(consolidated)
            if (match != null && VinDecoder.isValidVin(match.value)) {
                foundVin = match.value
            }
        }

        // Repair OCR substitutions (e.g. 'O' -> '0', 'Q' -> '0', 'I' -> '1') on 17-char candidates
        if (foundVin == null) {
            val tokens = fullText.split("\\s+".toRegex())
            for (token in tokens) {
                val cleaned = token.replace("-", "").replace(".", "").uppercase()
                if (cleaned.length == 17) {
                    val repaired = repairOcrVinConfusions(cleaned)
                    if (standardVinRegex.matches(repaired) && VinDecoder.isValidVin(repaired)) {
                        foundVin = repaired
                        break
                    }
                }
            }
        }

        return OcrScanResult(
            detectedVin = foundVin,
            detectedPlate = null,
            detectedLicenseDiscExpiry = null,
            detectedMake = foundMake,
            rawExtractedLines = allLines,
            scanSource = "Dashboard / Windshield VIN Plate",
            confidenceSummary = if (foundVin != null) "High Confidence Dashboard VIN" else "Low"
        )
    }

    /**
     * Extracts VIN, plate, expiry, make, and register number from a South African MVLX License Disc.
     */
    private fun parseLicenseDisc(visionText: Text): OcrScanResult {
        val allLines = mutableListOf<String>()
        var foundVin: String? = null
        var foundPlate: String? = null
        var foundExpiry: String? = null
        var foundMake: String? = null
        var foundSeries: String? = null
        var foundEngineNo: String? = null
        var foundRegisterNo: String? = null

        val fullText = visionText.text

        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val text = line.text.trim()
                allLines.add(text)
                val upper = text.uppercase()

                // 1. VIN Extraction: look for standard 17-char VIN or "VIN / VIN" label
                if (foundVin == null) {
                    val normalized = normalizeVinCandidate(text)
                    val match = standardVinRegex.find(normalized)
                    if (match != null && VinDecoder.isValidVin(match.value)) {
                        foundVin = match.value
                    } else if (upper.contains("VIN") || upper.contains("VOERTUIG") || upper.contains("CHASSIS")) {
                        val candidate = normalizeVinCandidate(upper.substringAfter("VIN").trim())
                        val cMatch = standardVinRegex.find(candidate)
                        if (cMatch != null && VinDecoder.isValidVin(cMatch.value)) {
                            foundVin = cMatch.value
                        }
                    }
                }

                // 2. Number Plate Extraction
                if (foundPlate == null) {
                    for (regex in plateRegexList) {
                        val match = regex.find(text)
                        if (match != null) {
                            val candidate = match.value.trim().uppercase()
                            if (!candidate.contains("DISC") && !candidate.contains("NATIS") && candidate.length in 5..14) {
                                foundPlate = candidate
                                break
                            }
                        }
                    }
                }

                // 3. Expiry Date: YYYY-MM-DD or DD/MM/YYYY
                if (foundExpiry == null) {
                    val exp1 = expiryRegex.find(text)
                    if (exp1 != null) {
                        foundExpiry = exp1.value
                    } else {
                        val exp2 = altExpiryRegex.find(text)
                        if (exp2 != null) {
                            foundExpiry = exp2.value
                        }
                    }
                }

                // 4. Vehicle Make
                if (foundMake == null) {
                    foundMake = detectVehicleMake(text)
                }

                // 5. Engine Number (e.g. "1GD-FTV-882194" or "EA211-58291")
                if (foundEngineNo == null && (upper.contains("ENJIN") || upper.contains("ENGINE"))) {
                    val candidate = text.substringAfter(":").substringAfter("NO").trim()
                    if (candidate.length in 6..20) {
                        foundEngineNo = candidate
                    }
                }

                // 6. Register Number (e.g. "NATIS-ZA-99218274")
                if (foundRegisterNo == null && (upper.contains("REGISTER") || upper.contains("NATIS") || upper.contains("REG NO"))) {
                    val candidate = text.substringAfter(":").trim()
                    if (candidate.isNotEmpty()) {
                        foundRegisterNo = candidate
                    }
                }
            }
        }

        // Consolidated text fallback for VIN
        if (foundVin == null) {
            val consolidated = normalizeVinCandidate(fullText)
            val match = standardVinRegex.find(consolidated)
            if (match != null && VinDecoder.isValidVin(match.value)) {
                foundVin = match.value
            }
        }

        // Consolidated text fallback for Plate
        if (foundPlate == null) {
            for (regex in plateRegexList) {
                val match = regex.find(fullText)
                if (match != null) {
                    val candidate = match.value.trim().uppercase()
                    if (!candidate.contains("DISC") && !candidate.contains("NATIS")) {
                        foundPlate = candidate
                        break
                    }
                }
            }
        }

        // Auto-correlate Plate to VIN if only Plate was detected on South African disc
        if (foundVin == null && foundPlate != null) {
            try {
                foundVin = VinDecoder.lookupByPlate(foundPlate).vin
            } catch (e: Exception) {
                // Ignore if lookup fails
            }
        }

        return OcrScanResult(
            detectedVin = foundVin,
            detectedPlate = foundPlate,
            detectedLicenseDiscExpiry = foundExpiry,
            detectedMake = foundMake,
            detectedSeries = foundSeries,
            detectedEngineNumber = foundEngineNo,
            detectedRegisterNumber = foundRegisterNo,
            rawExtractedLines = allLines,
            scanSource = "South African MVLX License Disc",
            confidenceSummary = if (foundVin != null && foundPlate != null) "Full MVLX Disc Verified" else if (foundVin != null || foundPlate != null) "Partial Disc Match" else "Unverified"
        )
    }

    /**
     * Removes noise, spaces, dashes, and periods to prepare candidate for VIN regex matching.
     */
    private fun normalizeVinCandidate(raw: String): String {
        return raw
            .replace(" ", "")
            .replace("-", "")
            .replace(".", "")
            .replace("/", "")
            .replace("\n", "")
            .uppercase()
    }

    /**
     * Repairs common optical OCR confusions on stamped VIN metal plates:
     * - 'O' and 'Q' are replaced with '0' (ISO 3779 forbids letters O and Q)
     * - 'I' is replaced with '1' (ISO 3779 forbids letter I)
     */
    private fun repairOcrVinConfusions(raw: String): String {
        return raw.map { c ->
            when (c) {
                'O', 'Q' -> '0'
                'I' -> '1'
                else -> c
            }
        }.joinToString("")
    }

    private fun detectVehicleMake(text: String): String? {
        val upper = text.uppercase()
        return when {
            upper.contains("TOYOTA") -> "Toyota"
            upper.contains("VOLKSWAGEN") || upper.contains("VW") -> "Volkswagen"
            upper.contains("FORD") -> "Ford"
            upper.contains("BMW") -> "BMW"
            upper.contains("MERCEDES") -> "Mercedes-Benz"
            upper.contains("ISUZU") -> "Isuzu"
            upper.contains("NISSAN") -> "Nissan"
            upper.contains("HYUNDAI") -> "Hyundai"
            upper.contains("RENAULT") -> "Renault"
            upper.contains("KIA") -> "Kia"
            upper.contains("SUZUKI") -> "Suzuki"
            upper.contains("MAZDA") -> "Mazda"
            else -> null
        }
    }
}
