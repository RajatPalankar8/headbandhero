package com.proto.headbandhero.game

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

object SoundEffectPlayer {
    var isMuted: Boolean = false
    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 22050

    fun playJump() {
        if (isMuted) return
        scope.launch {
            playSquareTone(260f, 520f, 0.12f)
        }
    }

    fun playDoubleJump() {
        if (isMuted) return
        scope.launch {
            playSquareTone(390f, 740f, 0.14f)
        }
    }

    fun playStomp() {
        if (isMuted) return
        scope.launch {
            playSquareTone(320f, 140f, 0.10f)
            playSquareTone(460f, 620f, 0.08f)
        }
    }

    fun playCoin() {
        if (isMuted) return
        scope.launch {
            playSquareTone(660f, 990f, 0.09f)
        }
    }

    fun playWin() {
        if (isMuted) return
        scope.launch {
            playSquareTone(440f, 523f, 0.10f)
            playSquareTone(523f, 659f, 0.10f)
            playSquareTone(659f, 784f, 0.18f)
        }
    }

    fun playHit() {
        if (isMuted) return
        scope.launch {
            playSquareTone(220f, 80f, 0.16f)
        }
    }

    private fun playSquareTone(freqStart: Float, freqEnd: Float, durationSec: Float) {
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        if (numSamples <= 0) return
        val buffer = ShortArray(numSamples)

        var phase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = freqStart * (freqEnd / freqStart).toDouble().pow(progress)
            val phaseInc = 2.0 * PI * currentFreq / SAMPLE_RATE
            phase += phaseInc

            val raw = if (sin(phase) >= 0) 0.5 else -0.5
            val envelope = (1.0 - progress) * (1.0 - progress)
            val sampleValue = (raw * envelope * 8000.0).toInt().coerceIn(-32768, 32767)
            buffer[i] = sampleValue.toShort()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(numSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, numSamples)
            audioTrack.play()
            Thread.sleep((durationSec * 1000).toLong() + 50)
            audioTrack.release()
        } catch (e: Exception) {
            // AudioTrack allocation exception fallback
        }
    }
}
