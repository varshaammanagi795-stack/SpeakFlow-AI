package com.example.data.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TTSManager(context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                textToSpeech?.language = Locale.US
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }
                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                    }
                })
            }
        }
    }

    fun speak(
        text: String,
        accent: String = "US",
        pitch: Float = 1.0f,
        speechRate: Float = 0.95f
    ) {
        if (!isInitialized) return

        val locale = when (accent.uppercase()) {
            "UK" -> Locale.UK
            "AU" -> Locale("en", "AU")
            "CA" -> Locale.CANADA
            else -> Locale.US
        }

        try {
            textToSpeech?.apply {
                language = locale
                setPitch(pitch)
                setSpeechRate(speechRate)
                speak(text, TextToSpeech.QUEUE_FLUSH, null, "SpeakFlow_Utterance_${System.currentTimeMillis()}")
            }
        } catch (_: Exception) {}
    }

    fun stop() {
        try {
            textToSpeech?.stop()
            _isSpeaking.value = false
        } catch (_: Exception) {}
    }

    fun shutdown() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (_: Exception) {}
    }
}
