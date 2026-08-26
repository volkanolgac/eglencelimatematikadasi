package com.example.carpimadasi.logic

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundManager(context: Context) {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(Dispatchers.Default)

    var soundEnabled: Boolean = true
    var musicEnabled: Boolean = true

    private val sampleRate = 22050

    private fun generateTone(
        freq: Double,
        durationSeconds: Double,
        type: String = "sine",
        volume: Double = 0.25
    ): ShortArray {
        val numSamples = (sampleRate * durationSeconds).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val raw = when (type) {
                "triangle" -> {
                    val p = (t * freq) % 1.0
                    if (p < 0.5) (4.0 * p - 1.0) else (3.0 - 4.0 * p)
                }
                "square" -> {
                    if (sin(2.0 * PI * freq * t) >= 0) 1.0 else -1.0
                }
                "sawtooth" -> {
                    2.0 * ((t * freq) % 1.0) - 1.0
                }
                else -> { // sine
                    sin(2.0 * PI * freq * t)
                }
            }

            // Envelope: fast linear attack, exponential decay
            val attackSamples = (sampleRate * 0.01).toInt()
            val env = if (i < attackSamples) {
                i.toDouble() / attackSamples
            } else {
                val decayTime = (i - attackSamples).toDouble() / (numSamples - attackSamples)
                Math.exp(-3.5 * decayTime)
            }

            val sampleVal = (raw * env * volume * Short.MAX_VALUE).toInt().coerceIn(
                Short.MIN_VALUE.toInt(),
                Short.MAX_VALUE.toInt()
            )
            samples[i] = sampleVal.toShort()
        }
        return samples
    }

    private fun playPcm(samples: ShortArray) {
        if (!soundEnabled) return
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()
            track.setNotificationMarkerPosition(samples.size)
            track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onPeriodicNotification(t: AudioTrack?) {}
                override fun onMarkerReached(t: AudioTrack?) {
                    t?.release()
                }
            })
        } catch (_: Exception) {
        }
    }

    fun click() {
        if (!soundEnabled) return
        scope.launch {
            val tone = generateTone(440.0, 0.05, "square", 0.15)
            playPcm(tone)
        }
    }

    fun correct() {
        if (!soundEnabled) return
        scope.launch {
            // Cheerful 3-note chime C5, E5, G5
            val t1 = generateTone(523.25, 0.10, "sine", 0.22)
            val t2 = generateTone(659.25, 0.10, "sine", 0.22)
            val t3 = generateTone(783.99, 0.18, "sine", 0.24)
            val combined = ShortArray(t1.size + t2.size + t3.size)
            System.arraycopy(t1, 0, combined, 0, t1.size)
            System.arraycopy(t2, 0, combined, t1.size, t2.size)
            System.arraycopy(t3, 0, combined, t1.size + t2.size, t3.size)
            playPcm(combined)
        }
    }

    fun combo(multiplier: Int) {
        if (!soundEnabled) return
        scope.launch {
            // Distinct sparkling, energetic 5-note combo fanfare
            val baseFreq = if (multiplier >= 5) 587.33 else 523.25 // Higher pitch for huge combos
            val notes = listOf(
                baseFreq to 0.07,
                baseFreq * 1.2599 to 0.07,
                baseFreq * 1.4983 to 0.08,
                baseFreq * 2.0 to 0.09,
                baseFreq * 2.5198 to 0.24
            )
            val buffers = notes.map { (f, d) -> generateTone(f, d, "triangle", 0.26) }
            val totalSize = buffers.sumOf { it.size }
            val combined = ShortArray(totalSize)
            var pos = 0
            for (buf in buffers) {
                System.arraycopy(buf, 0, combined, pos, buf.size)
                pos += buf.size
            }
            playPcm(combined)
        }
    }

    fun wrong() {
        if (!soundEnabled) return
        scope.launch {
            val t1 = generateTone(311.13, 0.15, "sawtooth", 0.14)
            val t2 = generateTone(233.08, 0.22, "sawtooth", 0.14)
            val combined = ShortArray(t1.size + t2.size)
            System.arraycopy(t1, 0, combined, 0, t1.size)
            System.arraycopy(t2, 0, combined, t1.size, t2.size)
            playPcm(combined)
        }
    }

    fun diamond() {
        if (!soundEnabled) return
        scope.launch {
            val t1 = generateTone(987.77, 0.08, "triangle", 0.18)
            val t2 = generateTone(1318.51, 0.14, "triangle", 0.18)
            val combined = ShortArray(t1.size + t2.size)
            System.arraycopy(t1, 0, combined, 0, t1.size)
            System.arraycopy(t2, 0, combined, t1.size, t2.size)
            playPcm(combined)
        }
    }

    fun chest() {
        if (!soundEnabled) return
        scope.launch {
            val t1 = generateTone(523.25, 0.10, "triangle", 0.2)
            val t2 = generateTone(659.25, 0.10, "triangle", 0.2)
            val t3 = generateTone(783.99, 0.12, "triangle", 0.2)
            val t4 = generateTone(1046.50, 0.28, "triangle", 0.22)
            val combined = ShortArray(t1.size + t2.size + t3.size + t4.size)
            var offset = 0
            System.arraycopy(t1, 0, combined, offset, t1.size); offset += t1.size
            System.arraycopy(t2, 0, combined, offset, t2.size); offset += t2.size
            System.arraycopy(t3, 0, combined, offset, t3.size); offset += t3.size
            System.arraycopy(t4, 0, combined, offset, t4.size)
            playPcm(combined)
        }
    }

    fun victory() {
        if (!soundEnabled) return
        scope.launch {
            val notes = listOf(
                523.25 to 0.12,
                659.25 to 0.12,
                783.99 to 0.14,
                1046.50 to 0.20,
                1318.50 to 0.25,
                1567.90 to 0.50
            )
            val buffers = notes.map { (f, d) -> generateTone(f, d, "triangle", 0.25) }
            val totalSize = buffers.sumOf { it.size }
            val combined = ShortArray(totalSize)
            var pos = 0
            for (buf in buffers) {
                System.arraycopy(buf, 0, combined, pos, buf.size)
                pos += buf.size
            }
            playPcm(combined)
        }
    }

    fun gameOver() {
        if (!soundEnabled) return
        scope.launch {
            val notes = listOf(
                329.63 to 0.25,
                293.66 to 0.25,
                261.63 to 0.30,
                220.00 to 0.55
            )
            val buffers = notes.map { (f, d) -> generateTone(f, d, "triangle", 0.2) }
            val totalSize = buffers.sumOf { it.size }
            val combined = ShortArray(totalSize)
            var pos = 0
            for (buf in buffers) {
                System.arraycopy(buf, 0, combined, pos, buf.size)
                pos += buf.size
            }
            playPcm(combined)
        }
    }

    fun release() {
        // AudioTrack instances are released after playback
    }
}

