package com.example.data

import android.content.Context
import android.media.MediaPlayer
import android.util.Base64
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AudioPlayerHelper(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPlayingId = MutableStateFlow<String?>(null)
    val currentPlayingId: StateFlow<String?> = _currentPlayingId.asStateFlow()

    fun playBase64Audio(id: String, base64Audio: String, mimeType: String = "audio/wav", onCompletion: () -> Unit = {}) {
        try {
            stop()

            val rawBytes = Base64.decode(base64Audio, Base64.DEFAULT)
            if (rawBytes == null || rawBytes.isEmpty()) {
                _isPlaying.value = false
                _currentPlayingId.value = null
                return
            }

            // Ensure proper WAV header if raw PCM
            val audioBytes = if (isWavOrMp3(rawBytes)) {
                rawBytes
            } else {
                wrapPcmWithWavHeader(rawBytes, sampleRate = 24000, channels = 1, bitDepth = 16)
            }

            val tempFile = File(context.cacheDir, "tts_${System.currentTimeMillis()}.wav")
            FileOutputStream(tempFile).use { fos ->
                fos.write(audioBytes)
            }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                setOnPreparedListener {
                    start()
                    _isPlaying.value = true
                    _currentPlayingId.value = id
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPlayingId.value = null
                    it.release()
                    mediaPlayer = null
                    tempFile.delete()
                    onCompletion()
                }
                setOnErrorListener { mp, _, _ ->
                    _isPlaying.value = false
                    _currentPlayingId.value = null
                    mp.release()
                    mediaPlayer = null
                    tempFile.delete()
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
            _currentPlayingId.value = null
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _isPlaying.value = false
            _currentPlayingId.value = null
        }
    }

    private fun isWavOrMp3(bytes: ByteArray): Boolean {
        if (bytes.size < 4) return false
        val prefix = String(bytes.copyOfRange(0, 4))
        if (prefix == "RIFF" || prefix == "ID3") return true
        // MP3 frame sync check (0xFF and high 3 bits set in 2nd byte)
        if (bytes[0] == 0xFF.toByte() && (bytes[1].toInt() and 0xE0) == 0xE0) return true
        return false
    }

    private fun wrapPcmWithWavHeader(
        pcmData: ByteArray,
        sampleRate: Int = 24000,
        channels: Short = 1,
        bitDepth: Short = 16
    ): ByteArray {
        val totalAudioLen = pcmData.size
        val totalDataLen = totalAudioLen + 36
        val byteRate = sampleRate * channels * bitDepth / 8
        val blockAlign = (channels * bitDepth / 8).toShort()

        val header = ByteArrayOutputStream(44)
        val buffer = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)

        // RIFF chunk descriptor
        buffer.put("RIFF".toByteArray(Charsets.US_ASCII))
        buffer.putInt(totalDataLen)
        buffer.put("WAVE".toByteArray(Charsets.US_ASCII))

        // "fmt " sub-chunk
        buffer.put("fmt ".toByteArray(Charsets.US_ASCII))
        buffer.putInt(16) // Subchunk1Size for PCM
        buffer.putShort(1) // AudioFormat 1 = PCM
        buffer.putShort(channels)
        buffer.putInt(sampleRate)
        buffer.putInt(byteRate)
        buffer.putShort(blockAlign)
        buffer.putShort(bitDepth)

        // "data" sub-chunk
        buffer.put("data".toByteArray(Charsets.US_ASCII))
        buffer.putInt(totalAudioLen)

        header.write(buffer.array())
        header.write(pcmData)

        return header.toByteArray()
    }
}
