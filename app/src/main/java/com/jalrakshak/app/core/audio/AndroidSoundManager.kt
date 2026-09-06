package com.jalrakshak.app.core.audio

import android.media.AudioManager
import android.media.ToneGenerator

class AndroidSoundManager : SoundManager {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 60)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    override fun playAlertSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
        } catch (_: Exception) {
            // Safe fallback
        }
    }
}
