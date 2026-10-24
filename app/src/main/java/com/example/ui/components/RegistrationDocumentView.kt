package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.VehicleDetails

@Composable
fun RegistrationDocumentDialog(
    vehicle: VehicleDetails,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "NaTIS Certificate of Registration (Form RC1)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Official National Traffic Information System Document Copy",
                                fontSize = 10.sp,
                                color = Color(0xFF10B981)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Document Sheet
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Document Certificate Container (Simulates official security paper)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFAFAFA), // Off-white security parchment
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1B5E20)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            // Top Official Government Header
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "REPUBLIC OF SOUTH AFRICA • REPUBLIEK VAN SUID-AFRIKA",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    color = Color(0xFF1B5E20),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "DEPARTMENT OF TRANSPORT • DEPARTEMENT VAN VERVOER",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF2E7D32),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "CERTIFICATE OF REGISTRATION IN RESPECT OF MOTOR VEHICLE",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "(National Road Traffic Act, 1996 • Form RC1 / NaTIS)",
                                    fontSize = 8.sp,
                                    color = Color(0xFF4B5563),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFF1B5E20), thickness = 1.5.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Control & Reference Numbers
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("CONTROL NO: RC1-${vehicle.vin.takeLast(7)}", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color.Black)
                                Text("STATUS: ${vehicle.natisStatus}", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B5E20))
                                Text("DATE: ${vehicle.registrationDetails.expiryDate.take(4)}-01-15", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // SECTION A: PARTICULARS OF REGISTERING AUTHORITY
                            DocSectionHeader("SECTION A: REGISTERING AUTHORITY")
                            DocRow("Registering Authority", vehicle.registrationDetails.registeringAuthorityDltc)
                            DocRow("Province of Registration", vehicle.province)
                            DocRow("Roadworthy Certificate No.", vehicle.registrationDetails.roadworthyCertificateNumber)
                            DocRow("Roadworthy Test Date", vehicle.registrationDetails.roadworthyTestDate)

                            Spacer(modifier = Modifier.height(10.dp))

                            // SECTION B: PARTICULARS OF MOTOR VEHICLE
                            DocSectionHeader("SECTION B: PARTICULARS OF MOTOR VEHICLE")
                            DocRow("Licence Number (Plate)", vehicle.registrationNumber, isBold = true)
                            DocRow("Vehicle Identification No (VIN)", vehicle.vin, isBold = true, isMono = true)
                            DocRow("Engine Number", vehicle.engineNumber, isMono = true)
                            DocRow("Make & Model", "${vehicle.make} ${vehicle.model} ${vehicle.derivative}")
                            DocRow("Vehicle Category / Nature", "${vehicle.bodyType} (${vehicle.accidentCode.take(6)})")
                            DocRow("Tare (T) / Gross Vehicle Mass", "T: ${vehicle.tareKg} kg | GVM: ${vehicle.gvmKg} kg")
                            DocRow("Engine Capacity & Fuel", "${vehicle.engineCapacityCc} • ${vehicle.fuelType}")
                            DocRow("Manufacturing Assembly Plant", vehicle.assemblyPlant)
                            DocRow("Microdot Encryption Provider", vehicle.microdotProvider)

                            Spacer(modifier = Modifier.height(10.dp))

                            // SECTION C: TITLEHOLDER PARTICULARS (BANK / OWNER)
                            DocSectionHeader("SECTION C: TITLEHOLDER PARTICULARS")
                            DocRow("Titleholder Name", vehicle.titleholder, isBold = true)
                            DocRow("Institution / Type", if (vehicle.isFinanced) "Registered Financial Institution (${vehicle.financeInstitution})" else "Private Registered Owner")
                            DocRow("Settlement Status", vehicle.settlementStatus)

                            Spacer(modifier = Modifier.height(10.dp))

                            // SECTION D: REGISTERED OWNER PARTICULARS
                            DocSectionHeader("SECTION D: REGISTERED OWNER PARTICULARS")
                            val owner = vehicle.currentOwner
                            DocRow("Owner Full Name", owner.fullName, isBold = true)
                            DocRow("Identity Number (Masked)", owner.maskedId, isMono = true)
                            DocRow("Ownership Classification", owner.ownershipType)
                            DocRow("Residential Address", owner.fullAddress)
                            DocRow("Province & Postal Code", "${owner.province} (${owner.postalCode})")
                            DocRow("Registration Date Acquired", owner.dateAcquired)

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFF1B5E20), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Official Government Stamp & Verification Seal
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("OFFICIAL NaTIS RECORD CERTIFIED", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                    Text("REGULATION 56 & ROAD TRAFFIC ACT 93 OF 1996", fontSize = 7.sp, color = Color(0xFF4B5563))
                                    Text("Barcoded Ref: ${vehicle.registrationDetails.diskBarcodeNumber}", fontSize = 7.sp, fontFamily = FontFamily.Monospace, color = Color.Black)
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFE8F5E9),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1B5E20)),
                                    modifier = Modifier.size(52.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("NaTIS", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFF1B5E20))
                                            Text("VALID", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Actions Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "NaTIS RC1 Document exported for print / PDF", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print / Export RC1", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "RC1 Document details shared", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Document", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DocSectionHeader(title: String) {
    Surface(
        color = Color(0xFFE8F5E9),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = title,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1B5E20),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun DocRow(label: String, value: String, isBold: Boolean = false, isMono: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 8.5.sp, color = Color(0xFF4B5563), fontWeight = FontWeight.Normal)
        Text(
            text = value,
            fontSize = 9.sp,
            color = Color.Black,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default
        )
    }
}
