package com.example.gopetalk_clean.data.api

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioService @Inject constructor() {

    private val sampleRate = 16000

    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    ).coerceAtLeast(2048)

    private var recorder: AudioRecord? = null
    private var isRecording = false
    private var audioTrack: AudioTrack? = null

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording(onData: (ByteArray) -> Unit) {
        if (bufferSize <= 0) {
            throw IllegalStateException("Buffer inválido: $bufferSize")
        }

        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        if (recorder?.state != AudioRecord.STATE_INITIALIZED) {
            throw IllegalStateException("AudioRecord no se pudo inicializar")
        }

        recorder?.startRecording()
        isRecording = true

        Thread {
            val buffer = ByteArray(bufferSize)
            while (isRecording) {
                val read = recorder?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    onData(buffer.copyOf(read))
                }
            }
        }.start()
    }

    suspend fun stopRecording() = withContext(Dispatchers.IO) {
        isRecording = false
        recorder?.stop()
        recorder?.release()
        recorder = null
    }

    fun playAudio(data: ByteArray) {
        if (audioTrack == null) {
            val minBuffer = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(2048)

            audioTrack = AudioTrack.Builder()
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuffer)
                .build()

            audioTrack?.play()
        }
        audioTrack?.write(data, 0, data.size)
    }
}
