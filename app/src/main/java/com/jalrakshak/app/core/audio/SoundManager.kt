package com.jalrakshak.app.core.audio

/**
 * Interface for SoundPool/audio management abstractions.
 */
interface SoundManager {
    fun playAlertSound()
    fun release() {}
}
