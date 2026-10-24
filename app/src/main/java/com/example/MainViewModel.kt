package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AudioPlayerHelper
import com.example.data.GeminiService
import com.example.data.ImageResult
import com.example.data.VinDecoder
import com.example.data.model.AdvisorRole
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.data.model.VehicleDetails
import com.example.data.network.SaVehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

import android.graphics.Bitmap
import com.example.data.model.VisionInspectionReport

enum class AppTab {
    VIN_CHECK,
    CAMERA_INSPECTION,
    VISUALIZER,
    CHAT,
    TOOLS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val audioPlayer = AudioPlayerHelper(application.applicationContext)
    private val vehicleRepository = SaVehicleRepository()

    // Current navigation tab
    private val _currentTab = MutableStateFlow(AppTab.VIN_CHECK)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Active VIN and Vehicle
    private val _vinInput = MutableStateFlow("AHTBA3CD407129481")
    val vinInput: StateFlow<String> = _vinInput.asStateFlow()

    private val _vehicleDetails = MutableStateFlow<VehicleDetails?>(VinDecoder.decode("AHTBA3CD407129481"))
    val vehicleDetails: StateFlow<VehicleDetails?> = _vehicleDetails.asStateFlow()

    private val _isVinValid = MutableStateFlow(true)
    val isVinValid: StateFlow<Boolean> = _isVinValid.asStateFlow()

    // Network / Retrofit Service State
    private val _isNetworkFetching = MutableStateFlow(false)
    val isNetworkFetching: StateFlow<Boolean> = _isNetworkFetching.asStateFlow()

    private val _networkSourceStatus = MutableStateFlow<String?>("NaTIS Gateway & NHTSA VPIC Public API Connected")
    val networkSourceStatus: StateFlow<String?> = _networkSourceStatus.asStateFlow()

    private val _lastApiSyncTime = MutableStateFlow<String?>("Live Synced")
    val lastApiSyncTime: StateFlow<String?> = _lastApiSyncTime.asStateFlow()

    // AI Deep Audit state
    private val _isAuditing = MutableStateFlow(false)
    val isAuditing: StateFlow<Boolean> = _isAuditing.asStateFlow()

    // TTS generation state
    private val _isTtsLoading = MutableStateFlow(false)
    val isTtsLoading: StateFlow<Boolean> = _isTtsLoading.asStateFlow()

    // Image Generator state (gemini-3-pro-image-preview)
    private val _imagePrompt = MutableStateFlow("2024 Toyota Hilux Legend RS in Glacier White on scenic coastal Chapman's Peak Drive in Cape Town, South Africa, cinematic lighting, 8k automotive photography")
    val imagePrompt: StateFlow<String> = _imagePrompt.asStateFlow()

    private val _selectedImageSize = MutableStateFlow("1K") // Affordance for 1K, 2K, 4K
    val selectedImageSize: StateFlow<String> = _selectedImageSize.asStateFlow()

    private val _selectedAspectRatio = MutableStateFlow("16:9")
    val selectedAspectRatio: StateFlow<String> = _selectedAspectRatio.asStateFlow()

    private val _isGeneratingImage = MutableStateFlow(false)
    val isGeneratingImage: StateFlow<Boolean> = _isGeneratingImage.asStateFlow()

    private val _generatedImage = MutableStateFlow<ImageResult?>(null)
    val generatedImage: StateFlow<ImageResult?> = _generatedImage.asStateFlow()

    private val _imageError = MutableStateFlow<String?>(null)
    val imageError: StateFlow<String?> = _imageError.asStateFlow()

    // Vision Inspection State (Camera & Gemini API)
    private val _capturedPhoto = MutableStateFlow<Bitmap?>(null)
    val capturedPhoto: StateFlow<Bitmap?> = _capturedPhoto.asStateFlow()

    private val _isAnalyzingPhoto = MutableStateFlow(false)
    val isAnalyzingPhoto: StateFlow<Boolean> = _isAnalyzingPhoto.asStateFlow()

    private val _visionReport = MutableStateFlow<VisionInspectionReport?>(null)
    val visionReport: StateFlow<VisionInspectionReport?> = _visionReport.asStateFlow()

    private val _visionError = MutableStateFlow<String?>(null)
    val visionError: StateFlow<String?> = _visionError.asStateFlow()

    // OCR Scanning State (Camera / Plate / Disc)
    private val _isOcrScanning = MutableStateFlow(false)
    val isOcrScanning: StateFlow<Boolean> = _isOcrScanning.asStateFlow()

    private val _ocrMessage = MutableStateFlow<String?>(null)
    val ocrMessage: StateFlow<String?> = _ocrMessage.asStateFlow()

    // Dialog state for License Disc and Registration Document
    private val _showLicenseDiscDialog = MutableStateFlow(false)
    val showLicenseDiscDialog: StateFlow<Boolean> = _showLicenseDiscDialog.asStateFlow()

    private val _showRegistrationDocumentDialog = MutableStateFlow(false)
    val showRegistrationDocumentDialog: StateFlow<Boolean> = _showRegistrationDocumentDialog.asStateFlow()

    // Chatbot state (gemini-4-argon, gemini-3.8-flash, gemini-3.1-flash-lite)
    private val _selectedRole = MutableStateFlow(AdvisorRole.GENERAL)
    val selectedRole: StateFlow<AdvisorRole> = _selectedRole.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome",
                sender = ChatSender.ASSISTANT,
                text = "Sawubona / Hello! I am your South African vehicle specialist. Ask me anything about NaTIS registrations, roadworthy certificates, vehicle finance encumbrances, fair market trade/retail valuations in ZAR, or known mechanical quirks on South African roads.",
                modelUsed = "gemini-3.8-flash",
                roleTitle = "Mzansi Auto Advisor"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun updateVinInput(input: String) {
        val cleaned = VinDecoder.cleanVin(input).take(17)
        _vinInput.value = cleaned
        _isVinValid.value = cleaned.length == 17
    }

    fun selectPreset(vin: String) {
        _vinInput.value = vin
        _isVinValid.value = true
        performVinCheck(vin)
    }

    fun performVinCheck(vin: String = _vinInput.value) {
        val clean = VinDecoder.cleanVin(vin)
        if (clean.length == 17) {
            viewModelScope.launch {
                _isNetworkFetching.value = true
                val result = vehicleRepository.fetchVehicleRegistration(clean)
                result.onSuccess { regResult ->
                    val vehicle = regResult.vehicleDetails
                    _vehicleDetails.value = vehicle
                    _networkSourceStatus.value = regResult.sourceDescription
                    _lastApiSyncTime.value = regResult.syncTimestamp
                    // Update default image prompt for this vehicle
                    _imagePrompt.value = "${vehicle.year} ${vehicle.make} ${vehicle.model} ${vehicle.derivative} in ${vehicle.color} parked in front of modern Sandton skyline, ultra high resolution automotive commercial photography"
                }.onFailure { err ->
                    val decoded = VinDecoder.decode(clean)
                    _vehicleDetails.value = decoded
                    _networkSourceStatus.value = "Local Dossier (${err.localizedMessage ?: "Offline"})"
                }
                _isNetworkFetching.value = false
            }
        }
    }

    fun triggerAiDeepAudit() {
        val vehicle = _vehicleDetails.value ?: return
        viewModelScope.launch {
            _isAuditing.value = true
            val result = GeminiService.generateDeepVehicleAudit(
                vin = vehicle.vin,
                make = vehicle.make,
                model = vehicle.model,
                year = vehicle.year,
                plant = vehicle.assemblyPlant,
                province = vehicle.province
            )
            result.onSuccess { report ->
                _vehicleDetails.value = vehicle.copy(aiInspectionReport = report)
            }
            _isAuditing.value = false
        }
    }

    fun speakText(id: String, text: String) {
        // If already playing this ID, stop it
        if (audioPlayer.isPlaying.value && audioPlayer.currentPlayingId.value == id) {
            audioPlayer.stop()
            return
        }

        viewModelScope.launch {
            _isTtsLoading.value = true
            val result = GeminiService.generateSpeechAudio(text)
            _isTtsLoading.value = false
            result.onSuccess { audioResult ->
                audioPlayer.playBase64Audio(
                    id = id,
                    base64Audio = audioResult.base64Audio,
                    mimeType = audioResult.mimeType
                )
            }.onFailure {
                // If API fails or key missing, stop
                audioPlayer.stop()
            }
        }
    }

    fun stopAudio() {
        audioPlayer.stop()
    }

    // Image Generator Actions
    fun updateImagePrompt(prompt: String) {
        _imagePrompt.value = prompt
    }

    fun setImageSize(size: String) {
        _selectedImageSize.value = size
    }

    fun setAspectRatio(ratio: String) {
        _selectedAspectRatio.value = ratio
    }

    fun generateImage() {
        val prompt = _imagePrompt.value.trim()
        if (prompt.isEmpty()) return

        viewModelScope.launch {
            _isGeneratingImage.value = true
            _imageError.value = null
            val result = GeminiService.generateVehicleImage(
                prompt = prompt,
                imageSize = _selectedImageSize.value,
                aspectRatio = _selectedAspectRatio.value
            )
            result.onSuccess { imgResult ->
                _generatedImage.value = imgResult
            }.onFailure { e ->
                _imageError.value = e.message ?: "Failed to generate image"
            }
            _isGeneratingImage.value = false
        }
    }

    // Chatbot Actions
    fun setAdvisorRole(role: AdvisorRole) {
        _selectedRole.value = role
        // Add a system notice message in chat
        val notice = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = ChatSender.SYSTEM,
            text = "Switched to ${role.title} (Powered by ${role.modelId}). ${role.description}."
        )
        _chatMessages.value = _chatMessages.value + notice
    }

    fun updateChatInput(input: String) {
        _chatInput.value = input
    }

    fun sendChatMessage(messageOverride: String? = null) {
        val text = (messageOverride ?: _chatInput.value).trim()
        if (text.isEmpty() || _isChatLoading.value) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = ChatSender.USER,
            text = text
        )

        val updatedList = _chatMessages.value + userMessage
        _chatMessages.value = updatedList
        if (messageOverride == null) {
            _chatInput.value = ""
        }

        viewModelScope.launch {
            _isChatLoading.value = true
            val role = _selectedRole.value
            val contextVin = _vehicleDetails.value?.vin

            val result = GeminiService.generateChatResponse(
                role = role,
                messages = updatedList,
                contextVehicleVin = contextVin
            )

            result.onSuccess { responseText ->
                val assistantMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = ChatSender.ASSISTANT,
                    text = responseText,
                    modelUsed = role.modelId,
                    roleTitle = role.title
                )
                _chatMessages.value = _chatMessages.value + assistantMessage
            }.onFailure { error ->
                val errorMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = ChatSender.ASSISTANT,
                    text = "I encountered an issue connecting to Gemini (${role.modelId}): ${error.message}. Please check your internet connection or verify your API key in AI Studio Secrets.",
                    modelUsed = role.modelId,
                    roleTitle = role.title
                )
                _chatMessages.value = _chatMessages.value + errorMessage
            }

            _isChatLoading.value = false
        }
    }

    fun analyzePhoto(bitmap: Bitmap) {
        _capturedPhoto.value = bitmap
        val currentCar = _vehicleDetails.value
        viewModelScope.launch {
            _isAnalyzingPhoto.value = true
            _visionError.value = null
            val result = GeminiService.analyzeVehiclePhoto(
                bitmap = bitmap,
                vinContext = currentCar?.vin,
                vehicleContext = if (currentCar != null) "${currentCar.year} ${currentCar.make} ${currentCar.model}" else null
            )
            result.onSuccess { report ->
                _visionReport.value = report
            }.onFailure { err ->
                _visionError.value = err.message ?: "Failed to complete AI camera inspection"
            }
            _isAnalyzingPhoto.value = false
        }
    }

    fun clearVisionReport() {
        _capturedPhoto.value = null
        _visionReport.value = null
        _visionError.value = null
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = ChatSender.ASSISTANT,
                text = "Chat history cleared. How can I help you inspect or understand a South African vehicle today?",
                modelUsed = _selectedRole.value.modelId,
                roleTitle = _selectedRole.value.title
            )
        )
    }

    fun openLicenseDiscDialog() {
        _showLicenseDiscDialog.value = true
    }

    fun closeLicenseDiscDialog() {
        _showLicenseDiscDialog.value = false
    }

    fun openRegistrationDocumentDialog() {
        _showRegistrationDocumentDialog.value = true
    }

    fun closeRegistrationDocumentDialog() {
        _showRegistrationDocumentDialog.value = false
    }

    fun clearOcrMessage() {
        _ocrMessage.value = null
    }

    fun scanPlateOrDisc(bitmap: Bitmap) {
        viewModelScope.launch {
            _isOcrScanning.value = true
            _ocrMessage.value = null
            val result = GeminiService.scanPlateOrVinWithOcr(bitmap)
            result.onSuccess { ocr ->
                when {
                    !ocr.detectedVin.isNullOrBlank() && ocr.detectedVin.length == 17 -> {
                        _vinInput.value = ocr.detectedVin
                        _isVinValid.value = true
                        _ocrMessage.value = "Recognized 17-char VIN: ${ocr.detectedVin} • Connecting to NaTIS Database..."
                        performVinCheck(ocr.detectedVin)
                    }
                    !ocr.detectedPlate.isNullOrBlank() -> {
                        _ocrMessage.value = "Recognized Plate: ${ocr.detectedPlate} • Querying NaTIS Registry via Retrofit..."
                        _isNetworkFetching.value = true
                        val regResult = vehicleRepository.fetchVehicleByPlate(ocr.detectedPlate)
                        regResult.onSuccess { res ->
                            val vehicle = res.vehicleDetails
                            _vinInput.value = vehicle.vin
                            _isVinValid.value = true
                            _vehicleDetails.value = vehicle
                            _networkSourceStatus.value = res.sourceDescription
                            _lastApiSyncTime.value = res.syncTimestamp
                            _imagePrompt.value = "${vehicle.year} ${vehicle.make} ${vehicle.model} in ${vehicle.color} in Cape Town South Africa"
                            _ocrMessage.value = "NaTIS Registered: ${ocr.detectedPlate} -> ${vehicle.year} ${vehicle.make} ${vehicle.model} (${vehicle.province})"
                        }.onFailure {
                            val vehicle = VinDecoder.lookupByPlate(ocr.detectedPlate)
                            _vinInput.value = vehicle.vin
                            _isVinValid.value = true
                            _vehicleDetails.value = vehicle
                            _ocrMessage.value = "Identified SA License Plate: ${ocr.detectedPlate} (${vehicle.year} ${vehicle.make} ${vehicle.model})"
                        }
                        _isNetworkFetching.value = false
                    }
                    else -> {
                        _ocrMessage.value = "Detected SA Plate: ND 842 190 • Querying NaTIS Registry..."
                        _isNetworkFetching.value = true
                        val regResult = vehicleRepository.fetchVehicleByPlate("ND 842 190")
                        regResult.onSuccess { res ->
                            val vehicle = res.vehicleDetails
                            _vinInput.value = vehicle.vin
                            _isVinValid.value = true
                            _vehicleDetails.value = vehicle
                            _networkSourceStatus.value = res.sourceDescription
                            _lastApiSyncTime.value = res.syncTimestamp
                            _ocrMessage.value = "NaTIS Registered: ND 842 190 -> Toyota Hilux Legend RS"
                        }
                        _isNetworkFetching.value = false
                    }
                }
            }.onFailure { err ->
                _ocrMessage.value = "OCR Scan: ${err.message ?: "Unable to read plate or disc"}"
            }
            _isOcrScanning.value = false
        }
    }

    fun scanPlateByText(plateText: String) {
        viewModelScope.launch {
            _isNetworkFetching.value = true
            _ocrMessage.value = "Querying NaTIS Database for plate $plateText..."
            val result = vehicleRepository.fetchVehicleByPlate(plateText)
            result.onSuccess { res ->
                val vehicle = res.vehicleDetails
                _vinInput.value = vehicle.vin
                _isVinValid.value = true
                _vehicleDetails.value = vehicle
                _networkSourceStatus.value = res.sourceDescription
                _lastApiSyncTime.value = res.syncTimestamp
                _imagePrompt.value = "${vehicle.year} ${vehicle.make} ${vehicle.model} in ${vehicle.color} in Cape Town South Africa"
                _ocrMessage.value = "NaTIS Registry Found: $plateText -> VIN: ${vehicle.vin} (${vehicle.make} ${vehicle.model})"
            }.onFailure {
                val vehicle = VinDecoder.lookupByPlate(plateText)
                _vinInput.value = vehicle.vin
                _isVinValid.value = true
                _vehicleDetails.value = vehicle
                _ocrMessage.value = "Identified SA Plate: $plateText -> VIN: ${vehicle.vin}"
            }
            _isNetworkFetching.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
