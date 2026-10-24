package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.ui.components.LicenseDiscDialog
import com.example.ui.components.RegistrationDocumentDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AppTab
import com.example.MainViewModel
import com.example.data.VinDecoder
import com.example.data.model.InsuranceClaim
import com.example.data.model.OwnerHistoryRecord
import com.example.data.model.VehicleDetails
import com.example.ui.components.BadgeStatusType
import com.example.ui.components.DetailRow
import com.example.ui.components.SectionCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.formatZar
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

data class SaPlatePreset(val plate: String, val modelName: String, val province: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VinCheckScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val vinInput by viewModel.vinInput.collectAsState()
    val isVinValid by viewModel.isVinValid.collectAsState()
    val vehicleDetails by viewModel.vehicleDetails.collectAsState()
    val isAuditing by viewModel.isAuditing.collectAsState()
    val isTtsLoading by viewModel.isTtsLoading.collectAsState()
    val isAudioPlaying by viewModel.audioPlayer.isPlaying.collectAsState()
    val currentPlayingId by viewModel.audioPlayer.currentPlayingId.collectAsState()
    val isOcrScanning by viewModel.isOcrScanning.collectAsState()
    val ocrMessage by viewModel.ocrMessage.collectAsState()
    val showLicenseDiscDialog by viewModel.showLicenseDiscDialog.collectAsState()
    val showRegistrationDocumentDialog by viewModel.showRegistrationDocumentDialog.collectAsState()
    val isNetworkFetching by viewModel.isNetworkFetching.collectAsState()
    val networkSourceStatus by viewModel.networkSourceStatus.collectAsState()
    val lastApiSyncTime by viewModel.lastApiSyncTime.collectAsState()
    val context = LocalContext.current

    // Camera take picture preview launcher for plate / disc OCR
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.scanPlateOrDisc(bitmap)
        }
    }

    // Permission launcher for camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission needed to scan license plate", Toast.LENGTH_SHORT).show()
        }
    }

    // Photo picker launcher (zero-permission Android Photo Picker)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    if (bmp != null) {
                        viewModel.scanPlateOrDisc(bmp)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val saPlatePresets = remember {
        listOf(
            SaPlatePreset("ND 842 190", "Hilux 4x4", "KZN"),
            SaPlatePreset("CA 529 881", "Polo Vivo", "WC"),
            SaPlatePreset("JH 21 XK GP", "Ranger Wildtrak", "GP"),
            SaPlatePreset("FC 88 TW GP", "BMW 3 Series", "GP"),
            SaPlatePreset("DZ 91 KK EC", "Isuzu D-Max", "EC")
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Search Input Header
            Text(
                text = "South African VIN Verification",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Decode 17-character VIN with official NaTIS registration, owner history, accident records & license disc status",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate400
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Input Field
            OutlinedTextField(
                value = vinInput,
                onValueChange = { viewModel.updateVinInput(it) },
                label = { Text("Enter 17-Character VIN") },
                placeholder = { Text("e.g. AHTBA3CD407129481") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "Car",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (vinInput.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.updateVinInput("") },
                            modifier = Modifier.testTag("clear_vin_button")
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (vinInput.length == 17) "Standard ISO 3779 South Africa VIN Valid" else "Enter exactly 17 characters (${vinInput.length}/17)",
                            color = if (vinInput.length == 17) EmeraldGreen else Slate400,
                            fontSize = 11.sp
                        )
                        if (vinInput.isNotEmpty() && (vinInput.contains("I") || vinInput.contains("O") || vinInput.contains("Q"))) {
                            Text("Note: I, O, Q replaced by standard", color = AmberWarning, fontSize = 11.sp)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Slate700,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vin_input_field")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: Search Button + Camera OCR Scan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.performVinCheck() },
                    enabled = vinInput.length == 17 && !isNetworkFetching,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .testTag("run_vin_check_button")
                ) {
                    if (isNetworkFetching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Querying...", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Search NaTIS", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch(null)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    enabled = !isOcrScanning,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Slate800,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("camera_ocr_scan_button")
                ) {
                    if (isOcrScanning) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Plate OCR", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    enabled = !isOcrScanning,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(0.7f)
                        .height(48.dp)
                        .testTag("photo_ocr_picker_button")
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Pick Photo", modifier = Modifier.size(18.dp))
                }
            }

            // OCR Status Feedback Banner
            ocrMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearOcrMessage() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Dismiss", modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // South African License Plate Direct OCR/Lookup Chips
            Spacer(modifier = Modifier.height(10.dp))
            Column {
                Text(
                    text = "South African Number Plates (Direct OCR Lookup):",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(saPlatePresets) { preset ->
                        val plate = preset.plate
                        val modelName = preset.modelName
                        val province = preset.province
                        val isCurrentPlate = vehicleDetails?.registrationNumber == plate
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCurrentPlate) EmeraldGreen else Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isCurrentPlate) EmeraldGreen else Color.Black),
                            modifier = Modifier
                                .clickable { viewModel.scanPlateByText(plate) }
                                .testTag("plate_chip_${plate.replace(" ", "_")}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = plate,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = if (isCurrentPlate) Color.White else Color.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• $modelName ($province)",
                                    fontSize = 10.sp,
                                    color = if (isCurrentPlate) Color.White else Slate700
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Presets
        item {
            Column {
                Text(
                    text = "Sample South African Vehicles (NaTIS Presets):",
                    style = MaterialTheme.typography.labelMedium,
                    color = Slate400,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(VinDecoder.PRESETS) { preset ->
                        val isSelected = vinInput.equals(preset.vin, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectPreset(preset.vin) },
                            label = {
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(preset.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(preset.subtitle, fontSize = 10.sp, color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Slate400)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("preset_${preset.vin.take(5)}")
                        )
                    }
                }
            }
        }

        // Vehicle Report Result
        vehicleDetails?.let { vehicle ->
            // Hero Vehicle Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${vehicle.year} ${vehicle.make} ${vehicle.model}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = vehicle.derivative,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // South African License Plate Component
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.Black),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = vehicle.registrationNumber,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Retrofit Database Service Status Bar
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isNetworkFetching) AmberWarning else EmeraldGreen)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (isNetworkFetching) "Querying NaTIS Gateway..." else "NaTIS Live Gateway & Public VPIC API",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.CloudDone,
                                                contentDescription = null,
                                                tint = EmeraldGreen,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                        Text(
                                            text = "${networkSourceStatus ?: "South African Registry"} • ${lastApiSyncTime ?: "Live Synced"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 10.sp,
                                            color = Slate400
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.performVinCheck() },
                                    enabled = !isNetworkFetching,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .testTag("refresh_vehicle_api_button")
                                ) {
                                    if (isNetworkFetching) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Re-query Database",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Status badges row
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            StatusBadge(
                                text = "NaTIS: ${vehicle.natisStatus}",
                                statusType = BadgeStatusType.SUCCESS
                            )
                            StatusBadge(
                                text = "Disk: ${vehicle.registrationDetails.licenseDiskStatus.take(14)}",
                                statusType = if (vehicle.registrationDetails.licenseDiskStatus.contains("VALID")) BadgeStatusType.SUCCESS else BadgeStatusType.WARNING
                            )
                            StatusBadge(
                                text = if (vehicle.accidentInsuranceRecord.hasClearHistory) "Accident: CLEAR" else "Accident: CLAIMS RECORDED",
                                statusType = if (vehicle.accidentInsuranceRecord.hasClearHistory) BadgeStatusType.SUCCESS else BadgeStatusType.WARNING
                            )
                            StatusBadge(
                                text = "SAPS: CLEAR",
                                statusType = BadgeStatusType.SUCCESS
                            )
                            StatusBadge(
                                text = if (vehicle.isFinanced) "Finance: ${vehicle.financeInstitution}" else "Finance: Clear Title",
                                statusType = if (vehicle.isFinanced) BadgeStatusType.WARNING else BadgeStatusType.SUCCESS
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Slate800)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Listen Audio Report Button (Gemini TTS gemini-3.8-flash-tts)
                            Button(
                                onClick = {
                                    val summaryToSpeak = "VinCheck South Africa Report for ${vehicle.year} ${vehicle.make} ${vehicle.model}. " +
                                            "Assembled at ${vehicle.assemblyPlant}. " +
                                            "License disk status is ${vehicle.registrationDetails.licenseDiskStatus}, expiry date ${vehicle.registrationDetails.expiryDate}. " +
                                            "Registered owner of record is ${vehicle.currentOwner.fullName} in ${vehicle.currentOwner.city}, ${vehicle.currentOwner.province}. " +
                                            if (vehicle.accidentInsuranceRecord.hasClearHistory) "Accident history is clear with zero major collisions or flood damage. " else "Accident records indicate ${vehicle.accidentInsuranceRecord.majorAccidentsCount} reported incidents. " +
                                            if (vehicle.isFinanced) "Active financial interest by ${vehicle.financeInstitution}. Settlement required. " else "Clear titleholder with zero bank encumbrance. " +
                                            "Estimated retail market value is ${formatZar(vehicle.retailValueZar)}."
                                    viewModel.speakText("vehicle_report", summaryToSpeak)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAudioPlaying && currentPlayingId == "vehicle_report") CrimsonError else MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = if (isAudioPlaying && currentPlayingId == "vehicle_report") Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("listen_tts_button")
                            ) {
                                if (isTtsLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Synthesizing...", fontSize = 12.sp)
                                } else if (isAudioPlaying && currentPlayingId == "vehicle_report") {
                                    Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Stop Audio", fontSize = 12.sp)
                                } else {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Listen (TTS)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // AI Deep Audit Button
                            OutlinedButton(
                                onClick = { viewModel.triggerAiDeepAudit() },
                                shape = RoundedCornerShape(10.dp),
                                enabled = !isAuditing,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ai_audit_button")
                            ) {
                                if (isAuditing) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Auditing...", fontSize = 12.sp)
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AI Audit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.updateImagePrompt("${vehicle.year} ${vehicle.make} ${vehicle.model} in ${vehicle.color} in Cape Town South Africa with Table Mountain backdrop")
                                    viewModel.setTab(AppTab.VISUALIZER)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Visualizer", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.sendChatMessage("Can you review the ownership history and accident report for this ${vehicle.year} ${vehicle.make} ${vehicle.model}?")
                                    viewModel.setTab(AppTab.CHAT)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ask Advisor", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // South African Official Vehicle Document Viewers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openLicenseDiscDialog() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldGreen,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("view_license_disc_button")
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("License Disc Copy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.openRegistrationDocumentDialog() },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Slate800,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("view_registration_doc_button")
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("NaTIS RC1 Document", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // MODULE 1: South African Vehicle Registration & License Disk Database (NaTIS)
            item {
                SectionCard(
                    title = "Vehicle Registration & License Disk (NaTIS)",
                    icon = Icons.Default.FactCheck,
                    headerBadge = {
                        StatusBadge(
                            text = vehicle.registrationDetails.licenseDiskStatus,
                            statusType = if (vehicle.registrationDetails.licenseDiskStatus.contains("VALID")) BadgeStatusType.SUCCESS else BadgeStatusType.WARNING
                        )
                    }
                ) {
                    DetailRow(label = "Registration / Plate Number", value = vehicle.registrationNumber, highlight = true)
                    DetailRow(label = "License Disk Status", value = vehicle.registrationDetails.licenseDiskStatus, highlight = true)
                    DetailRow(label = "License Disk Expiry Date", value = vehicle.registrationDetails.expiryDate)
                    DetailRow(label = "Days Until Disk Expiry", value = "${vehicle.registrationDetails.daysRemaining} Days remaining")
                    DetailRow(label = "Encrypted Disk Barcode (MVLX)", value = vehicle.registrationDetails.diskBarcodeNumber)
                    DetailRow(label = "Registering Authority (DLTC)", value = vehicle.registrationDetails.registeringAuthorityDltc)
                    DetailRow(label = "Roadworthy Certificate (COR)", value = vehicle.registrationDetails.roadworthyCertificateNumber)
                    DetailRow(label = "Roadworthy Test Date", value = vehicle.registrationDetails.roadworthyTestDate)
                    DetailRow(label = "Tare Weight", value = "${vehicle.tareKg} kg")
                    DetailRow(label = "Gross Vehicle Mass (GVM)", value = "${vehicle.gvmKg} kg")

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.openLicenseDiscDialog() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("module1_view_disc_button")
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open MVLX Disc", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.openRegistrationDocumentDialog() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("module1_view_rc1_button")
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open RC1 Cert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // MODULE 2: Current Registered Owner & Address
            item {
                SectionCard(
                    title = "Registered Owner of Record",
                    icon = Icons.Default.Person,
                    headerBadge = {
                        StatusBadge("ID Verified", BadgeStatusType.SUCCESS)
                    }
                ) {
                    val owner = vehicle.currentOwner
                    DetailRow(label = "First Name", value = owner.firstName)
                    DetailRow(label = "Surname", value = owner.surname, highlight = true)
                    DetailRow(label = "Full Name", value = owner.fullName, highlight = true)
                    DetailRow(label = "South African ID Number", value = owner.maskedId)
                    DetailRow(label = "Ownership Classification", value = owner.ownershipType)
                    DetailRow(label = "Registration Acquisition Date", value = owner.dateAcquired)

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Slate800)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Registered Residential / Postal Address:", fontSize = 12.sp, color = Slate400)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = owner.fullAddress,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Province: ${owner.province} • Postal Code: ${owner.postalCode}",
                                fontSize = 12.sp,
                                color = Slate400
                            )
                        }
                    }
                }
            }

            // MODULE 3: Ownership History & Chain of Title
            item {
                SectionCard(
                    title = "Ownership History (${vehicle.ownershipHistory.size} Records)",
                    icon = Icons.Default.Timeline,
                    headerBadge = {
                        StatusBadge("${vehicle.ownershipHistory.size} Owners", BadgeStatusType.INFO)
                    }
                ) {
                    Text(
                        text = "Historical titleholder sequence retrieved from South African vehicle registry:",
                        fontSize = 12.sp,
                        color = Slate400
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    vehicle.ownershipHistory.forEachIndexed { index, record ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(22.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${record.ownerNumber}",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = record.ownerName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Slate800
                                    ) {
                                        Text(
                                            text = record.periodYears,
                                            fontSize = 10.sp,
                                            color = Slate400,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Type: ${record.ownershipType} • Location: ${record.location}",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                                Text(
                                    text = "Dates: ${record.startDate} → ${record.endDate}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Transfer: ${record.transferReason}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // MODULE 4: Accident & Insurance History Module
            item {
                SectionCard(
                    title = "Accident & Insurance History",
                    icon = Icons.Default.ReportProblem,
                    headerBadge = {
                        StatusBadge(
                            text = if (vehicle.accidentInsuranceRecord.hasClearHistory) "CLEAR HISTORY" else "CLAIMS LOGGED",
                            statusType = if (vehicle.accidentInsuranceRecord.hasClearHistory) BadgeStatusType.SUCCESS else BadgeStatusType.WARNING
                        )
                    }
                ) {
                    val record = vehicle.accidentInsuranceRecord

                    // Clear history banner
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (record.hasClearHistory) EmeraldGreen.copy(alpha = 0.12f) else AmberWarning.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (record.hasClearHistory) EmeraldGreen.copy(alpha = 0.5f) else AmberWarning.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (record.hasClearHistory) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (record.hasClearHistory) EmeraldGreen else AmberWarning,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (record.hasClearHistory) "Clear Accident History Verified" else "Accident / Insurance Claim Found",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (record.hasClearHistory) EmeraldGreen else AmberWarning
                                )
                                Text(
                                    text = if (record.hasClearHistory) "No major collision damage, structural pull marks, or write-off flags detected on SA databases."
                                    else "This vehicle has reported insurance collision or rebuild claims. Review SAMBRA details below.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    DetailRow(label = "Reported Major Accidents", value = "${record.majorAccidentsCount} Incidents")
                    DetailRow(
                        label = "Flood & Water Damage Status",
                        value = record.floodDamageStatus,
                        valueColor = if (record.isFloodDamaged) CrimsonError else EmeraldGreen,
                        highlight = true
                    )
                    DetailRow(
                        label = "Insurance Total-Loss Write-Off",
                        value = record.writeOffStatus,
                        valueColor = if (record.isInsuranceWriteOff) CrimsonError else EmeraldGreen,
                        highlight = record.isInsuranceWriteOff
                    )
                    DetailRow(label = "Airbag Deployment Check", value = vehicle.airbagStatus)

                    if (record.claimsHistory.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Reported Insurance Claims Breakdown:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        record.claimsHistory.forEach { claim ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = claim.incidentType,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = formatZar(claim.repairCostZar),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Date: ${claim.date} • Severity: ${claim.severity}",
                                        fontSize = 11.sp,
                                        color = Slate400
                                    )
                                    Text(
                                        text = "Repairer: ${claim.sambraRepairer}",
                                        fontSize = 11.sp,
                                        color = Slate400
                                    )
                                    Text(
                                        text = if (claim.structuralFrameAffected) "WARNING: Structural frame benchmark affected" else "Chassis structure untouched (Cosmetic panels only)",
                                        fontSize = 11.sp,
                                        color = if (claim.structuralFrameAffected) CrimsonError else EmeraldGreen,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // MODULE 5: Core Mechanical & Manufacturing Plant Details
            item {
                SectionCard(
                    title = "Vehicle Engine & Manufacturing Plant",
                    icon = Icons.Default.Build
                ) {
                    DetailRow(label = "VIN (17 Characters)", value = vehicle.vin, highlight = true)
                    DetailRow(label = "Make & Model", value = "${vehicle.make} ${vehicle.model}")
                    DetailRow(label = "Model Year", value = "${vehicle.year}")
                    DetailRow(label = "Derivative / Trim", value = vehicle.derivative)
                    DetailRow(label = "Assembly Plant", value = vehicle.assemblyPlant, highlight = true)
                    DetailRow(label = "Engine Type & Capacity", value = "${vehicle.fuelType} (${vehicle.engineCapacityCc})")
                    DetailRow(label = "Power & Torque", value = vehicle.powerKw)
                    DetailRow(label = "Transmission", value = vehicle.transmission)
                    DetailRow(label = "Drivetrain", value = vehicle.drivetrain)
                    DetailRow(label = "Exterior Color", value = vehicle.color)
                    DetailRow(label = "Engine Serial Number", value = vehicle.engineNumber)
                }
            }

            // MODULE 6: SAPS Stolen Vehicle Check
            item {
                SectionCard(
                    title = "SAPS Stolen Vehicle Check",
                    icon = Icons.Default.LocalPolice,
                    headerBadge = {
                        StatusBadge("Police: CLEAR", BadgeStatusType.SUCCESS)
                    }
                ) {
                    DetailRow(label = "Circulation Status", value = vehicle.sapsStatus, highlight = true, valueColor = EmeraldGreen)
                    DetailRow(label = "Interpol Cross-Border Check", value = "CLEARED (SADC Region)")
                    DetailRow(label = "SAPS 263 Clearance Needed?", value = if (vehicle.sapsClearanceRequired) "YES (Action Required)" else "NO (Standard Record)")
                    DetailRow(label = "Chassis Tampering Flag", value = "NEGATIVE (Original Factory Stamping)")
                }
            }

            // MODULE 7: Financial Interest / Encumbrance Section
            item {
                SectionCard(
                    title = "Financial Interest & Titleholder",
                    icon = Icons.Default.AccountBalance,
                    headerBadge = {
                        StatusBadge(
                            text = if (vehicle.isFinanced) "ENCUMBERED" else "CLEAR TITLE",
                            statusType = if (vehicle.isFinanced) BadgeStatusType.WARNING else BadgeStatusType.SUCCESS
                        )
                    }
                ) {
                    DetailRow(label = "Bank / Financier", value = vehicle.financeInstitution, highlight = vehicle.isFinanced)
                    DetailRow(label = "Titleholder of Record", value = vehicle.titleholder)
                    DetailRow(label = "Settlement Status", value = vehicle.settlementStatus)

                    if (vehicle.isFinanced) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = AmberWarning.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Buyer Caution: This vehicle is under active finance. Demand an official bank settlement letter before making payment.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // MODULE 8: ZAR Market Valuation Card
            item {
                SectionCard(
                    title = "Estimated Market Value (South Africa)",
                    icon = Icons.Default.Payments
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Retail Value", fontSize = 11.sp, color = Slate400)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formatZar(vehicle.retailValueZar),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = EmeraldGreen
                                )
                                Text("Insurance / Dealer", fontSize = 10.sp, color = Slate400)
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Trade Value", fontSize = 11.sp, color = Slate400)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formatZar(vehicle.tradeValueZar),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Dealer Buy-In", fontSize = 10.sp, color = Slate400)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Private Sale", fontSize = 11.sp, color = Slate400)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formatZar(vehicle.privateValueZar),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text("Fair Peer-to-Peer", fontSize = 10.sp, color = Slate400)
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Auction Value", fontSize = 11.sp, color = Slate400)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = formatZar(vehicle.auctionValueZar),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text("Liquidation floor", fontSize = 10.sp, color = Slate400)
                            }
                        }
                    }
                }
            }

            // MODULE 9: South African Road & Theft Advisory
            item {
                SectionCard(
                    title = "South Africa Driving & Theft Advisory",
                    icon = Icons.Default.Warning
                ) {
                    Text(
                        text = vehicle.saRoadAdvisory,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // MODULE 10: AI Deep Forensic Audit Report Card
            vehicle.aiInspectionReport?.let { audit ->
                item {
                    SectionCard(
                        title = "AI Forensic Audit Report",
                        icon = Icons.Default.AutoAwesome,
                        headerBadge = {
                            IconButton(
                                onClick = {
                                    viewModel.speakText("audit_report", audit)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isAudioPlaying && currentPlayingId == "audit_report") Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = "Read audit aloud",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    ) {
                        Text(
                            text = audit,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // South African Vehicle License Disc (MVLX) Copy Dialog
    if (showLicenseDiscDialog && vehicleDetails != null) {
        LicenseDiscDialog(
            vehicle = vehicleDetails!!,
            onDismiss = { viewModel.closeLicenseDiscDialog() }
        )
    }

    // South African NaTIS Form RC1 (Certificate of Registration) Document Dialog
    if (showRegistrationDocumentDialog && vehicleDetails != null) {
        RegistrationDocumentDialog(
            vehicle = vehicleDetails!!,
            onDismiss = { viewModel.closeRegistrationDocumentDialog() }
        )
    }
}
}
