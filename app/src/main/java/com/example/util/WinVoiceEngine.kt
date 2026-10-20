package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class WinVoiceEngine(
    private val context: Context,
    private val onVoiceCommandRecognized: (String) -> Unit
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var speechRecognizer: SpeechRecognizer? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _voiceEnabled = MutableStateFlow(true)
    val voiceEnabled: StateFlow<Boolean> = _voiceEnabled.asStateFlow()

    private val _preferHindiVoice = MutableStateFlow(true)
    val preferHindiVoice: StateFlow<Boolean> = _preferHindiVoice.asStateFlow()

    private val _lastSpokenMessage = MutableStateFlow(
        "WinBox 10 Tayyar Hai! Koi bhi Windows 10 software upload karein, woh full screen mein run karega aur voice mein baat karega."
    )
    val lastSpokenMessage: StateFlow<String> = _lastSpokenMessage.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (_: Exception) {
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            applyLocale()
            speak(
                hindiText = "WinBox 10 system chalu ho gaya hai. Aap koi bhi Windows 10 software upload kar sakte hain, woh full screen mein chalega aur aapse voice mein baat karega.",
                englishText = "WinBox 10 system is ready. Upload any Windows 10 software to run in full screen with voice interaction."
            )
        }
    }

    private fun applyLocale() {
        val engine = tts ?: return
        val hindiLocale = Locale.forLanguageTag("hi-IN")
        val targetLocale = if (_preferHindiVoice.value) hindiLocale else Locale.US
        val result = engine.setLanguage(targetLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            engine.setLanguage(Locale.US)
        }
        engine.setSpeechRate(1.0f)
        engine.setPitch(1.02f)
    }

    fun toggleVoiceEnabled() {
        _voiceEnabled.value = !_voiceEnabled.value
        if (!_voiceEnabled.value) {
            tts?.stop()
            _isSpeaking.value = false
        } else {
            speak(
                hindiText = "Voice baat-cheet chalu ho gayi hai.",
                englishText = "Voice talk mode enabled."
            )
        }
    }

    fun toggleLanguagePreference() {
        _preferHindiVoice.value = !_preferHindiVoice.value
        applyLocale()
        speak(
            hindiText = "Ab main Hindi aur Hinglish voice mein baat karunga.",
            englishText = "Switched to English voice narration."
        )
    }

    fun speak(hindiText: String, englishText: String = hindiText) {
        val chosen = if (_preferHindiVoice.value) hindiText else englishText
        _lastSpokenMessage.value = chosen
        if (!_voiceEnabled.value || !isTtsReady) return
        try {
            applyLocale()
            _isSpeaking.value = true
            tts?.speak(chosen, TextToSpeech.QUEUE_FLUSH, null, "winbox_utt_${System.currentTimeMillis()}")
        } catch (_: Exception) {
            _isSpeaking.value = false
        }
    }

    fun startListening() {
        try {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                speak(
                    hindiText = "Aapke phone mein voice mic service uplabdh nahi hai, par aap niche voice command buttons दबा sakte hain.",
                    englishText = "Speech recognition service is unavailable on this device; use the quick voice command buttons."
                )
                return
            }
            if (speechRecognizer == null) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            _isListening.value = true
                        }
                        override fun onBeginningOfSpeech() {}
                        override fun onRmsChanged(rmsdB: Float) {}
                        override fun onBufferReceived(buffer: ByteArray?) {}
                        override fun onEndOfSpeech() {
                            _isListening.value = false
                        }
                        override fun onError(error: Int) {
                            _isListening.value = false
                        }
                        override fun onResults(results: Bundle?) {
                            _isListening.value = false
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val top = matches?.firstOrNull()
                            if (!top.isNullOrBlank()) {
                                onVoiceCommandRecognized(top)
                            }
                        }
                        override fun onPartialResults(partialResults: Bundle?) {}
                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    if (_preferHindiVoice.value) "hi-IN" else "en-US"
                )
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }
            _isListening.value = true
            speechRecognizer?.startListening(intent)
        } catch (_: Exception) {
            _isListening.value = false
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {
        }
        _isListening.value = false
    }

    fun shutdown() {
        try {
            speechRecognizer?.destroy()
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {
        }
    }
}
