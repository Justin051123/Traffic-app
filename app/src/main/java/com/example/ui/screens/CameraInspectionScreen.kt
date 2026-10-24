package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.MainViewModel
import com.example.data.model.VisionInspectionReport
import com.example.ui.components.BadgeStatusType
import com.example.ui.components.DetailRow
import com.example.ui.components.SectionCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CameraInspectionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val capturedPhoto by viewModel.capturedPhoto.collectAsState()
    val isAnalyzing by viewModel.isAnalyzingPhoto.collectAsState()
    val visionReport by viewModel.visionReport.collectAsState()
    val visionError by viewModel.visionError.collectAsState()
    val currentCar by viewModel.vehicleDetails.collectAsState()

    val isAudioPlaying by viewModel.audioPlayer.isPlaying.collectAsState()
    val currentPlayingId by viewModel.audioPlayer.currentPlayingId.collectAsState()
    val isTtsLoading by viewModel.isTtsLoading.collectAsState()

    // Camera take picture preview launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.analyzePhoto(bitmap)
        }
    }

    // Permission launcher for camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission required to capture vehicle photo", Toast.LENGTH_SHORT).show()
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
                        viewModel.analyzePhoto(bmp)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load selected photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AI Visual Vehicle Inspection",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Factual condition audit via Gemini 3.8 Vision",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Anti-Fraud & Legal Compliance Callout
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Slate900,
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = AmberWarning,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Commercial Fraud-Protection Standard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AmberWarning
                        )
                        Text(
                            text = "This AI visual assessor reports ONLY features and defects 100% visible in the photograph. Unseen mechanical parts (engine bay internals, gearbox, chassis underbody) are explicitly designated as Unverified to prevent false or fraudulent representations.",
                            fontSize = 11.sp,
                            color = Slate400,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Active VIN Context Pill
        currentCar?.let { car ->
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Auditing: ${car.year} ${car.make} ${car.model}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = car.vin,
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = Slate400
                        )
                    }
                }
            }
        }

        // Photo Capture & Upload Actions Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Capture or Select Vehicle Photo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Take a clear shot of the vehicle exterior, damaged panel, or license disc",
                        fontSize = 12.sp,
                        color = Slate400
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    cameraLauncher.launch(null)
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("camera_capture_button")
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Camera", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("photo_picker_button")
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Select Photo", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Test preset image buttons for emulator/browser users
                    Text(
                        text = "Or Test with Sample Vehicle Scenarios:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate400
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SamplePhotoChip(
                            label = "Clean Hilux 4x4",
                            colorHex = 0xFFFFFFFF,
                            caption = "Glacier White Hilux Front View"
                        ) { bmp ->
                            viewModel.analyzePhoto(bmp)
                        }

                        SamplePhotoChip(
                            label = "Polo Vivo Red",
                            colorHex = 0xFFDC2626,
                            caption = "Flash Red VW Hatchback"
                        ) { bmp ->
                            viewModel.analyzePhoto(bmp)
                        }

                        SamplePhotoChip(
                            label = "Bumper Dent / Scratch",
                            colorHex = 0xFFF97316,
                            caption = "Orange Ranger Minor Bumper Scratch"
                        ) { bmp ->
                            viewModel.analyzePhoto(bmp)
                        }
                    }
                }
            }
        }

        // Loading Indicator while Gemini Multimodal analyzes
        if (isAnalyzing) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Gemini 3.8 Multimodal Vision Analysis in Progress...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Scanning panel gaps, paint reflection, headlamp clarity, and visible glass integrity",
                            fontSize = 12.sp,
                            color = Slate400,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Error message if any
        visionError?.let { err ->
            item {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = err, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
                    }
                }
            }
        }

        // Factual Inspection Report Output
        visionReport?.let { report ->
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header with Rating & Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Visual Condition Report",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = report.detectedVehicleDescription,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            StatusBadge(
                                text = report.overallVisualCondition,
                                statusType = if (report.overallVisualCondition.contains("Good", ignoreCase = true)) BadgeStatusType.SUCCESS else BadgeStatusType.WARNING
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Photo thumbnail display if present
                        report.photoBitmap?.let { bmp ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Slate950),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "Audited Vehicle Photograph",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // TTS Voice Button for Speech
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val speechText = "Certified Visual Vehicle Inspection for ${report.detectedVehicleDescription}. " +
                                            "Overall rating: ${report.overallVisualCondition}. " +
                                            "Verified observations: ${report.verifiedVisualFindings.joinToString(". ")}. " +
                                            "Visible defects: ${report.visibleDefects.joinToString(". ")}. " +
                                            "Notice: Internal engine, transmission, and chassis underside are unverified from photograph. " +
                                            "Legal verdict: ${report.legalComplianceVerdict}"
                                    viewModel.speakText("vision_report", speechText)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAudioPlaying && currentPlayingId == "vision_report") CrimsonError else MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = if (isAudioPlaying && currentPlayingId == "vision_report") Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isTtsLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Synthesizing...", fontSize = 12.sp)
                                } else if (isAudioPlaying && currentPlayingId == "vision_report") {
                                    Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Stop Audio", fontSize = 12.sp)
                                } else {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Listen Report (TTS)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = { viewModel.clearVisionReport() },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Retake", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Slate800)
                        Spacer(modifier = Modifier.height(12.dp))

                        // SECTION 1: VERIFIED VISUAL FINDINGS
                        Text(
                            text = "1. VERIFIED VISUAL FINDINGS (100% Confirmed):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = EmeraldGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        report.verifiedVisualFindings.forEach { finding ->
                            Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = finding, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // SECTION 2: VISIBLE DEFECTS & WEAR
                        Text(
                            text = "2. VISIBLE DEFECTS & WEAR INDICATORS:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AmberWarning
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        report.visibleDefects.forEach { defect ->
                            Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.ReportProblem, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = defect, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // SECTION 3: STRICTLY UNVERIFIED / INCONCLUSIVE (ANTI-FRAUD MANDATE)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Slate900,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VisibilityOff, contentDescription = null, tint = Slate400, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "3. STRICTLY UNVERIFIED AREAS (Not observable):",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Slate400
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                report.unverifiedAreas.forEach { area ->
                                    Text(
                                        text = "• $area",
                                        fontSize = 11.sp,
                                        color = Slate400,
                                        lineHeight = 15.sp,
                                        modifier = Modifier.padding(vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // SECTION 4: LEGAL VERDICT & NEXT STEPS
                        Text(
                            text = "4. COMMERCIAL VERDICT & DEKRA/AVTS GUIDANCE:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.legalComplianceVerdict,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SamplePhotoChip(
    label: String,
    colorHex: Long,
    caption: String,
    onBitmapGenerated: (Bitmap) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.clickable {
            // Generate a synthetic test vehicle bitmap with vehicle profile and test markings
            val width = 640
            val height = 480
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            canvas.drawColor(0xFF1E293B.toInt()) // Studio slate background

            val carPaint = Paint().apply {
                color = colorHex.toInt()
                isAntiAlias = true
            }
            // Draw car body silhouette
            canvas.drawRoundRect(100f, 220f, 540f, 380f, 30f, 30f, carPaint)
            canvas.drawRoundRect(180f, 130f, 440f, 240f, 40f, 40f, carPaint)

            // Draw wheels
            val wheelPaint = Paint().apply { color = 0xFF000000.toInt(); isAntiAlias = true }
            canvas.drawCircle(180f, 380f, 45f, wheelPaint)
            canvas.drawCircle(460f, 380f, 45f, wheelPaint)

            // Draw label on canvas
            val textPaint = Paint().apply {
                color = 0xFFFFFFFF.toInt()
                textSize = 24f
                isAntiAlias = true
            }
            canvas.drawText("VINCHECK SA TEST ASSET: $caption", 40f, 70f, textPaint)

            onBitmapGenerated(bmp)
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(colorHex))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
