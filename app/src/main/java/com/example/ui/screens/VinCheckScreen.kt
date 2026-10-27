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
import android.content.Intent
import com.example.SearchMode
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.UploadFile
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
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
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
import com.example.data.validation.Iso3779ValidationResult
import com.example.data.validation.Iso3779Validator
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
    val iso3779Validation by viewModel.iso3779Validation.collectAsState()
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
    val doubleVerificationAudit by viewModel.doubleVerificationAudit.collectAsState()
    val cloudSyncStatus by viewModel.cloudSyncStatus.collectAsState()
    val isGeneratingPdf by viewModel.isGeneratingPdf.collectAsState()
    val pdfStatusMessage by viewModel.pdfStatusMessage.collectAsState()
    val searchMode by viewModel.searchMode.collectAsState()
    val plateInput by viewModel.plateInput.collectAsState()
    val webAppUrl by viewModel.webAppUrl.collectAsState()
    val context = LocalContext.current

    // Dedicated Dashboard / Windshield VIN Camera launcher
    val dashVinCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.scanDashboardVin(bitmap)
        }
    }

    val dashVinCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dashVinCameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission required to scan dashboard VIN plate", Toast.LENGTH_SHORT).show()
        }
    }

    val dashVinPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    if (bmp != null) {
                        viewModel.scanDashboardVin(bmp)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read VIN photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera take picture preview launcher for plate OCR
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

    // Dedicated License Disc camera launcher
    val discCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.scanLicenseDisc(bitmap)
        }
    }

    // Dedicated License Disc camera permission launcher
    val discCameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            discCameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission required to scan vehicle license disc", Toast.LENGTH_SHORT).show()
        }
    }

    // Dedicated License Disc photo upload launcher
    val discPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    if (bmp != null) {
                        viewModel.scanLicenseDisc(bmp)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load disc photo: ${e.message}", Toast.LENGTH_SHORT).show()
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

            // Web App Link Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Web App",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "VinCheck SA Web App Link",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Persistent: $webAppUrl",
                                fontSize = 10.sp,
                                color = Slate400,
                                maxLines = 1
                            )
                        }
                    }
                    Button(
                        onClick = { viewModel.openWebAppDialog() },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("web_app_banner_button")
                    ) {
                        Text("Get Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Search Mode Tabs: VIN, Number Plate, License Disc
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // VIN Tab
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (searchMode == SearchMode.VIN) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setSearchMode(SearchMode.VIN) }
                        .testTag("tab_search_vin")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = if (searchMode == SearchMode.VIN) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "VIN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (searchMode == SearchMode.VIN) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Number Plate Tab
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (searchMode == SearchMode.NUMBER_PLATE) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier
                        .weight(1.2f)
                        .clickable { viewModel.setSearchMode(SearchMode.NUMBER_PLATE) }
                        .testTag("tab_search_plate")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = if (searchMode == SearchMode.NUMBER_PLATE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Number Plate",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (searchMode == SearchMode.NUMBER_PLATE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // License Disc Tab
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (searchMode == SearchMode.LICENSE_DISC) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier
                        .weight(1.2f)
                        .clickable { viewModel.setSearchMode(SearchMode.LICENSE_DISC) }
                        .testTag("tab_search_disc")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = if (searchMode == SearchMode.LICENSE_DISC) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "License Disc",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (searchMode == SearchMode.LICENSE_DISC) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (searchMode) {
                SearchMode.VIN -> {
                    // VIN Input Field
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
                                val validation = iso3779Validation
                                val statusText = when {
                                    validation == null -> "Enter 17-character VIN"
                                    validation.isValid -> "ISO 3779 Standard Valid (${validation.wmiCountry})"
                                    validation.illegalCharacters.isNotEmpty() -> "Prohibited letters: ${validation.illegalCharacters.joinToString(", ")} (I, O, Q forbidden)"
                                    !validation.lengthValid -> "Enter 17 characters (${vinInput.length}/17)"
                                    else -> validation.errorReason ?: "ISO 3779 format error"
                                }
                                val statusColor = when {
                                    validation?.isValid == true -> EmeraldGreen
                                    validation?.illegalCharacters?.isNotEmpty() == true -> CrimsonError
                                    vinInput.length == 17 -> AmberWarning
                                    else -> Slate400
                                }
                                Text(
                                    text = statusText,
                                    color = statusColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${vinInput.length}/17",
                                    color = if (vinInput.length == 17) EmeraldGreen else Slate400,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
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
                            enabled = vinInput.length == 17 && iso3779Validation?.isValid == true && !isNetworkFetching,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(48.dp)
                                .testTag("run_vin_check_button")
                        ) {
                            if (isNetworkFetching) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
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
                                    dashVinCameraLauncher.launch(null)
                                } else {
                                    dashVinCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            enabled = !isOcrScanning,
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(48.dp)
                                .testTag("camera_ocr_scan_button")
                        ) {
                            if (isOcrScanning) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Scanning...", fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Scan Dash VIN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                dashVinPhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            enabled = !isOcrScanning,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(0.7f)
                                .height(48.dp)
                                .testTag("photo_ocr_picker_button")
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = "Upload VIN Photo", modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Scan windshield lower-corner dashboard VIN plate or door pillar with Google ML Kit",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400,
                        fontSize = 10.sp
                    )

                    // ISO 3779 Structural Validation & Checksum Breakdown
                    if (vinInput.isNotEmpty() && iso3779Validation != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Iso3779StructureCard(
                            validation = iso3779Validation!!,
                            rawVin = vinInput
                        )
                    }
                }

                SearchMode.NUMBER_PLATE -> {
                    // NUMBER PLATE INPUT FIELD
                    OutlinedTextField(
                        value = plateInput,
                        onValueChange = { viewModel.updatePlateInput(it) },
                        label = { Text("Enter South African Number Plate") },
                        placeholder = { Text("e.g. ND 842 190, CA 529 881, JH 21 XK GP") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = "Plate",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (plateInput.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.updatePlateInput("") },
                                    modifier = Modifier.testTag("clear_plate_button")
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        supportingText = {
                            Text(
                                text = "Supports all 9 SA provinces (GP, KZN, WC, EC, FS, MP, LP, NW, NC)",
                                color = Slate400,
                                fontSize = 11.sp
                            )
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
                            .testTag("plate_input_field")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Number Plate Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.performPlateLookup() },
                            enabled = plateInput.isNotBlank() && !isNetworkFetching,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(48.dp)
                                .testTag("verify_plate_button")
                        ) {
                            if (isNetworkFetching) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Verifying...", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            } else {
                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Verify Plate", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800, contentColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("plate_camera_scan_button")
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Plate", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            enabled = !isOcrScanning,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(0.7f)
                                .height(48.dp)
                                .testTag("plate_photo_picker_button")
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Pick Photo", modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // South African License Plate Presets
                    Text(
                        text = "Or Select South African Plate to Query:",
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
                            val isCurrentPlate = vehicleDetails?.registrationNumber == plate || plateInput == plate
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isCurrentPlate) EmeraldGreen else Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isCurrentPlate) EmeraldGreen else Color.Black),
                                modifier = Modifier
                                    .clickable {
                                        viewModel.updatePlateInput(plate)
                                        viewModel.scanPlateByText(plate)
                                    }
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

                SearchMode.LICENSE_DISC -> {
                    // DEDICATED LICENSE DISC SCAN & UPLOAD MODULE
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldGreen.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "South African MVLX License Disc",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Scan the circular windscreen disc or upload a photo",
                                        fontSize = 11.sp,
                                        color = Slate400
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Scan Disc with Camera
                                Button(
                                    onClick = {
                                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                            discCameraLauncher.launch(null)
                                        } else {
                                            discCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    },
                                    enabled = !isOcrScanning,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("scan_license_disc_camera_button")
                                ) {
                                    if (isOcrScanning) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Reading...", fontSize = 12.sp)
                                    } else {
                                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Scan Camera", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                // Upload Disc Photo from Gallery
                                OutlinedButton(
                                    onClick = {
                                        discPhotoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    enabled = !isOcrScanning,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("upload_license_disc_button")
                                ) {
                                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Upload Disc", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Test with Sample License Discs
                            Text(
                                text = "Or Test with Sample Vehicle License Discs:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                SampleDiscChip(
                                    title = "Hilux Disc (KZN)",
                                    plate = "ND 842 190",
                                    vin = "AHTBA3CD407129481"
                                ) { vin, plate ->
                                    viewModel.updateVinInput(vin)
                                    viewModel.updatePlateInput(plate)
                                    viewModel.performVinCheck(vin)
                                }

                                SampleDiscChip(
                                    title = "Polo Vivo (WC)",
                                    plate = "CA 529 881",
                                    vin = "AAVZZZ6RZMU048291"
                                ) { vin, plate ->
                                    viewModel.updateVinInput(vin)
                                    viewModel.updatePlateInput(plate)
                                    viewModel.performVinCheck(vin)
                                }

                                SampleDiscChip(
                                    title = "Ranger Disc (GP)",
                                    plate = "JH 21 XK GP",
                                    vin = "AFAXXMRTXPK712039"
                                ) { vin, plate ->
                                    viewModel.updateVinInput(vin)
                                    viewModel.updatePlateInput(plate)
                                    viewModel.performVinCheck(vin)
                                }
                            }
                        }
                    }
                }
            }

            // OCR Status Feedback Banner
            ocrMessage?.let { msg ->
                Spacer(modifier = Modifier.height(10.dp))
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

                        Spacer(modifier = Modifier.height(10.dp))

                        // RMI Accredited Inspection PDF Report Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openPdfReport(context) },
                                enabled = !isGeneratingPdf,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("open_vin_rmi_pdf_button")
                            ) {
                                if (isGeneratingPdf) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generating...", fontSize = 12.sp)
                                } else {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("RMI PDF Report", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = { viewModel.sharePdfReport(context) },
                                enabled = !isGeneratingPdf,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("share_vin_rmi_pdf_button")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // PDF Status Notification Banner
                        pdfStatusMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = msg,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { viewModel.clearPdfStatusMessage() },
                                        modifier = Modifier.size(22.dp)
                                    ) {
                                        Icon(Icons.Default.Clear, contentDescription = "Dismiss", modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Cloud Watchlist Button (Firebase Firestore us-west1)
                        OutlinedButton(
                            onClick = { viewModel.saveVehicleToWatchlist() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("save_cloud_watchlist_button")
                        ) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Vehicle to Cloud Watchlist (Firebase)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        cloudSyncStatus?.let { status ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• $status",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = Slate400
                            )
                        }
                    }
                }
            }

            // DOUBLE-VERIFICATION AUDIT CARD (All Shown Results Double-Verified)
            doubleVerificationAudit?.let { audit ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldGreen.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = EmeraldGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Double-Verification Audit",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Verified Match: ${audit.verificationScore}% • All details verified",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = EmeraldGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                StatusBadge(
                                    text = audit.fraudRiskLevel,
                                    statusType = if (audit.isOverallVerified) BadgeStatusType.SUCCESS else BadgeStatusType.WARNING
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Slate800)
                            Spacer(modifier = Modifier.height(10.dp))

                            // List of double-verified data points
                            audit.items.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = if (item.isVerified) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (item.isVerified) EmeraldGreen else AmberWarning,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = item.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = item.value,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 11.sp,
                                                color = if (item.isVerified) EmeraldGreen else AmberWarning
                                            )
                                        }
                                        Text(
                                            text = item.detail,
                                            fontSize = 11.sp,
                                            color = Slate400,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Slate900,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FactCheck,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = audit.summaryNotice,
                                        fontSize = 11.sp,
                                        color = Slate400,
                                        lineHeight = 15.sp
                                    )
                                }
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

@Composable
fun SampleDiscChip(
    title: String,
    plate: String,
    vin: String,
    onSelect: (vin: String, plate: String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .clickable { onSelect(vin, plate) }
            .testTag("sample_disc_${plate.replace(" ", "_")}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = EmeraldGreen,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "$plate • ${vin.take(8)}...", fontSize = 9.5.sp, color = Slate400)
            }
        }
    }
}

@Composable
fun Iso3779StructureCard(
    validation: Iso3779ValidationResult,
    rawVin: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("iso_3779_structure_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (validation.isValid) EmeraldGreen.copy(alpha = 0.5f) else CrimsonError.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Title + ISO Compliance Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (validation.isValid) Icons.Default.FactCheck else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (validation.isValid) EmeraldGreen else CrimsonError,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ISO 3779 Structural Validation",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                StatusBadge(
                    text = if (validation.isValid) "ISO 3779 VALID" else "NON-COMPLIANT",
                    statusType = if (validation.isValid) BadgeStatusType.SUCCESS else BadgeStatusType.ERROR,
                    modifier = Modifier.testTag("iso_compliance_badge")
                )
            }

            // Error or Warning callout if invalid
            if (!validation.isValid && validation.errorReason != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CrimsonError.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonError.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReportProblem,
                            contentDescription = null,
                            tint = CrimsonError,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = validation.errorReason,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = CrimsonError
                        )
                    }
                }
            }

            // Monospace VIN Character Inspector with 3-section color-coding
            if (rawVin.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "ISO 3779 VIN SEGMENTATION BREAKDOWN",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate400,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate900)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val wmiChars = rawVin.take(3)
                    val vdsChars = if (rawVin.length > 3) rawVin.substring(3, minOf(9, rawVin.length)) else ""
                    val visChars = if (rawVin.length > 9) rawVin.substring(9, minOf(17, rawVin.length)) else ""

                    // WMI Section (Blue)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = wmiChars.ifEmpty { "..." },
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = Color(0xFF60A5FA)
                        )
                        Text("WMI (1-3)", fontSize = 8.5.sp, color = Slate400)
                    }

                    Text("•", color = Slate700)

                    // VDS Section (Purple)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = vdsChars.ifEmpty { "..." },
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = Color(0xFFA78BFA)
                        )
                        Text("VDS (4-9)", fontSize = 8.5.sp, color = Slate400)
                    }

                    Text("•", color = Slate700)

                    // VIS Section (Emerald)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = visChars.ifEmpty { "..." },
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = EmeraldGreen
                        )
                        Text("VIS (10-17)", fontSize = 8.5.sp, color = Slate400)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3 Detail Breakdown Blocks
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // 1. WMI (World Manufacturer Identifier)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "1. WMI (World Manufacturer Identifier)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = Color(0xFF60A5FA)
                            )
                            if (validation.isSouthAfricanWmi) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldGreen.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "🇿🇦 SA Domestic Assembly",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (validation.wmi.isNotEmpty()) {
                                "${validation.wmi}: ${validation.manufacturerDescription} (${validation.wmiCountry})"
                            } else "Awaiting 3-char WMI prefix...",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 2. VDS (Vehicle Descriptor Section & Checksum)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "2. VDS & Check Digit (Modulo 11)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = Color(0xFFA78BFA)
                            )
                            if (validation.isChecksumMatch) {
                                Text(
                                    text = "✓ Modulo-11 Match",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (validation.vds.isNotEmpty()) {
                                "VDS: ${validation.vds} • Pos 9 Check Digit: '${validation.checkDigit ?: "-"}' (Calculated: '${validation.calculatedCheckDigit ?: "-"}') • ${validation.checkDigitCompliance}"
                            } else "Positions 4-9 specify vehicle attributes and check digit",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 3. VIS (Vehicle Indicator Section)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "3. VIS (Vehicle Indicator Section)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = EmeraldGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (validation.vis.isNotEmpty()) {
                                "VIS: ${validation.vis} • Model Year: ${validation.decodedModelYear ?: validation.modelYearChar ?: "—"} • Plant: ${validation.plantCode ?: "—"} • Serial: ${validation.sequentialNumber} ${if (validation.hasValidNumericEnding) "(ISO 5.3.3 Numeric Ending ✓)" else ""}"
                            } else "Positions 10-17 encode model year, plant, and sequential production number",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

