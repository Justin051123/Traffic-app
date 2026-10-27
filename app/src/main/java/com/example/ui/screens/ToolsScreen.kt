package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DetailRow
import com.example.ui.components.SectionCard
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800

@Composable
fun ToolsScreen(modifier: Modifier = Modifier) {
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
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "South Africa NaTIS & Law Guide",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Official procedures under the National Road Traffic Act",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400
                    )
                }
            }
        }

        // Web App Link Card
        item {
            val context = LocalContext.current
            val storage = remember { com.example.data.storage.PersistentStorage(context) }
            val webAppUrl = storage.getWebAppUrl()
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
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VinCheck SA Web App",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Persistent cloud access for web and mobile browsers",
                                fontSize = 11.sp,
                                color = Slate400
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = webAppUrl,
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "💾 Persistently configured across app sessions & devices",
                        fontSize = 10.5.sp,
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("VinCheck SA Web App", webAppUrl)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Web App link copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Link", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webAppUrl)).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open in Browser", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // NaTIS Forms Section
        item {
            SectionCard(
                title = "Essential NaTIS Forms",
                icon = Icons.Default.Assignment
            ) {
                FormItem(
                    name = "Form NCO (Yellow Form)",
                    subtitle = "Notification of Change of Ownership / Sale of Motor Vehicle",
                    desc = "Must be completed and signed by BOTH buyer and seller immediately upon transaction. The seller must hand Part A & B to their local registering authority within 21 days."
                )

                HorizontalDivider(color = Slate800, modifier = Modifier.padding(vertical = 10.dp))

                FormItem(
                    name = "Form RLV (Blue Form)",
                    subtitle = "Application for Registration and Licensing of Motor Vehicle",
                    desc = "Completed by the buyer along with certified copy of South African ID, proof of residence (utility bill under 3 months), and valid Roadworthy Certificate."
                )

                HorizontalDivider(color = Slate800, modifier = Modifier.padding(vertical = 10.dp))

                FormItem(
                    name = "Certificate of Registration (RC1 / Logbook)",
                    subtitle = "Original vehicle title deed document",
                    desc = "Verify original watermarked paper. Check whether 'Titleholder' states a financial bank (WesBank/MFC/Standard Bank/Absa) or a private individual."
                )

                HorizontalDivider(color = Slate800, modifier = Modifier.padding(vertical = 10.dp))

                FormItem(
                    name = "SAPS Clearance Certificate (SAPS 263)",
                    subtitle = "Police vehicle clearance",
                    desc = "Mandatory if the vehicle has undergone an engine replacement, microdot re-application, cross-border import, or Code 3 rebuilt homologation."
                )
            }
        }

        // South African Registration Plate Decoder
        item {
            SectionCard(
                title = "South Africa Provincial Plates",
                icon = Icons.Default.Map
            ) {
                DetailRow(label = "Gauteng (GP)", value = "AA 11 BB GP format")
                DetailRow(label = "Western Cape (WC)", value = "CAA (Cape Town), CA, CF, CK, WC")
                DetailRow(label = "KwaZulu-Natal (KZN)", value = "ND (Durban), NPN, PMB, ZN")
                DetailRow(label = "Eastern Cape (EC)", value = "EC suffix (e.g. DZ 12 EC)")
                DetailRow(label = "Free State (FS)", value = "FS suffix (e.g. BF 45 FS)")
                DetailRow(label = "Mpumalanga (MP)", value = "MP suffix (e.g. HP 99 MP)")
                DetailRow(label = "Limpopo (LP)", value = "L / LP suffix")
                DetailRow(label = "North West (NW)", value = "NW suffix (e.g. BR 12 NW)")
                DetailRow(label = "Northern Cape (NC)", value = "NC suffix (e.g. KM 01 NC)")
            }
        }

        // Safety Checklist for Buying a Used Car in SA
        item {
            SectionCard(
                title = "Safe Buying Checklist (Anti-Scam)",
                icon = Icons.Default.Shield
            ) {
                CheckStep(1, "Verify Titleholder", "If bank financed, demand an official Settlement Letter dated within 7 days. Pay the bank directly.")
                CheckStep(2, "Match 3 Physical VINs", "Dashboard tag, driver door B-pillar sticker, and stamped steel firewall must all match the license disc barcode.")
                CheckStep(3, "Microdot Verification", "Look for DataDot/Veridot sticker on window. Spray-on dots protect against cloned vehicles.")
                CheckStep(4, "Never Pay Cash in Isolated Spots", "Conclude transactions at reputable banks, police stations, or certified vehicle inspection stations (Dekra/AVTS).")
                CheckStep(5, "Roadworthy COR Test", "Insist on a fresh roadworthy test before concluding payment to avoid taking on undeclared defects.")
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun FormItem(name: String, subtitle: String, desc: String) {
    Column {
        Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
        Text(text = subtitle, fontWeight = FontWeight.Medium, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = desc, fontSize = 12.sp, color = Slate400, lineHeight = 16.sp)
    }
}

@Composable
fun CheckStep(number: Int, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "$number",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = description, fontSize = 12.sp, color = Slate400, lineHeight = 16.sp)
        }
    }
}
