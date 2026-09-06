package com.jalrakshak.app.core.haptics

import android.view.HapticFeedbackConstants
import android.view.View

class AndroidHapticFeedback(
    private val view: View? = null
) : JalRakshakHapticFeedback {

    override fun performWarningHaptic() {
        try {
            view?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        } catch (_: Exception) {
            // Safe fallback
        }
    }
}
