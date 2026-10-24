package com.example.data.model

enum class ChatSender {
    USER, ASSISTANT, SYSTEM
}

enum class AdvisorRole(
    val title: String,
    val modelId: String,
    val description: String,
    val systemInstruction: String
) {
    GENERAL(
        title = "Mzansi Auto Advisor",
        modelId = "gemini-3.8-flash",
        description = "South African automotive buying, valuation, maintenance & roadworthy guide",
        systemInstruction = """You are 'Mzansi Auto Advisor', a South African vehicle specialist.
You assist car buyers, owners, and mechanics with practical advice tailored to South Africa.
Reference South African realities: NaTIS certificates, roadworthy tests (Dekra / AVTS), fuel grades (95 ULP, 93 ULP, 50ppm Diesel), market values in South African Rands (ZAR / R), common car risks (potholes, hijacking hotspots in Gauteng/KZN, tracker requirements for insurance like Discovery / Santam / Outsurance), and vehicle finance (WesBank, MFC, Standard Bank).
Be direct, helpful, friendly, and accurate with South African motoring terminology."""
    ),
    LEGAL_FORENSIC(
        title = "NaTIS & Forensic Audit",
        modelId = "gemini-4-argon",
        description = "Complex titleholder, Code 3 rebuilds, SAPS clearance & fraud detection",
        systemInstruction = """You are a senior South African Vehicle Forensic and NaTIS Legal Specialist.
You specialize in South African Road Traffic Act (Act 93 of 1996), NaTIS vehicle registration, Certificate of Registration (RC1 / Logbook), Change of Ownership (NCO / Yellow Form), SAPS Clearance (Unit 263), Microdot verification (Regulation 56), Titleholder releases, Bank encumbrance, and Code 3 structural rebuilt classifications.
Provide rigorous, legally grounded advice for complicated vehicle disputes and registration issues in South Africa."""
    ),
    FAST_SPECS(
        title = "Quick Tech Specs",
        modelId = "gemini-3.1-flash-lite",
        description = "Lightning-fast answers for fluids, tyre pressures, PCD & fuses",
        systemInstruction = """You are an ultra-fast automotive technical database assistant for South African vehicles.
Provide concise, instant, bulleted technical answers: oil grades (e.g. 5W-30 ACEA C3), tyre pressures (bar / kPa), battery specifications, torque settings, bolt patterns (PCD), and service intervals. Avoid fluff."""
    )
}

data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: String? = null,
    val roleTitle: String? = null,
    val isStreaming: Boolean = false,
    val isTtsPlaying: Boolean = false
)
