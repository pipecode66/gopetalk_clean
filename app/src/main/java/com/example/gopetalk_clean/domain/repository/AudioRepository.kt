package com.example.gopetalk_clean.domain.repository

interface AudioRepository  {
    suspend fun startSending()
    suspend fun stopSending()
    fun playReceivedAudio(data: ByteArray)
    fun isUserTalking(): Boolean
}
