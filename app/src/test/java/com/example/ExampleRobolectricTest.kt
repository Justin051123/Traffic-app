package com.example

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.example.data.GeminiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("VinCheck SA", appName)
  }

  @Test
  fun testVisionInspectionAntiFraudMandate() = runTest {
    val testBmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    val result = GeminiService.analyzeVehiclePhoto(testBmp, "AHTBA3CD407129481", "Toyota Hilux")
    assertTrue(result.isSuccess)
    val report = result.getOrNull()
    assertNotNull(report)
    assertTrue(report!!.verifiedVisualFindings.isNotEmpty())
    assertTrue(report.unverifiedAreas.isNotEmpty())
    assertTrue(report.unverifiedAreas.any { it.contains("Engine", ignoreCase = true) || it.contains("Undercarriage", ignoreCase = true) })
  }

  @Test
  fun testOcrScanPlateOrVin() = runTest {
    val testBmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    val result = GeminiService.scanPlateOrVinWithOcr(testBmp)
    assertTrue(result.isSuccess)
    val ocr = result.getOrNull()
    assertNotNull(ocr)
    assertNotNull(ocr!!.detectedPlate ?: ocr.detectedVin)
  }

  @Test
  fun testGenerateRmiInspectionPdf() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val vehicle = com.example.data.VinDecoder.decode("AHTBA3CD407129481")
    assertNotNull(vehicle)
    val pdfFile = com.example.data.pdf.PdfReportGenerator.generateInspectionPdf(
        context = context,
        vehicle = vehicle!!
    )
    assertNotNull(pdfFile)
    assertTrue(pdfFile.exists())
    assertTrue(pdfFile.length() > 0)
    assertTrue(pdfFile.name.contains("RMI_Vehicle_Inspection_AHTBA3CD407129481.pdf"))
  }
}
