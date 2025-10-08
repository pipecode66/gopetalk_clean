package com.example.gopetalk_clean.data.audio

import android.util.Log
import com.example.gopetalk_clean.di.NetworkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import okio.ByteString.Companion.toByteString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val client: OkHttpClient,
    @NetworkModule.WebSocketUrl private val url: String
) : WebSocketListener() {

    private var webSocket: WebSocket? = null
    private val _incomingAudio = MutableSharedFlow<ByteArray>(extraBufferCapacity = 64)
    val incomingAudio: SharedFlow<ByteArray> = _incomingAudio

    fun connect(channel: String, userId: String, token: String) {
        val request = Request.Builder()
            .url("$url?channel=$channel&userId=$userId")
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, this)
        Log.d("WebSocketManager", "Intentando conectar a canal=$channel userId=$userId")
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnected")
        webSocket = null
    }

    fun sendAudio(data: ByteArray) {
        val byteString = data.toByteString(0, data.size)
        if (webSocket?.send(byteString) != true) {
            Log.e("WebSocketManager", "WebSocket es null o cerrado, no se puede enviar audio")
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("WebSocketManager", "Conexión WebSocket abierta")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        val data = bytes.toByteArray()
        CoroutineScope(Dispatchers.IO).launch {
            _incomingAudio.emit(data)
        }
        Log.d("WebSocketManager", "Audio recibido (${data.size} bytes)")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WebSocketManager", "Texto recibido: $text")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocketManager", "Cerrando conexión: $code - $reason")
        webSocket.close(1000, null)
        this.webSocket = null
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocketManager", "WebSocket cerrado: $code - $reason")
        this.webSocket = null
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSocketManager", "Error WebSocket: ${t.message}", t)
        this.webSocket = null
    }
}
