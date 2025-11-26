package com.example.gopetalk_clean.data.audio

import android.util.Log
import com.example.gopetalk_clean.di.NetworkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
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
        if (channel.isBlank()) {
            Log.w("WebSocketManager", "Channel name is blank, skipping connection")
            return
        }
        if (token.isBlank()) {
            Log.w("WebSocketManager", "Token is missing, skipping connection")
            return
        }

        webSocket?.close(1000, "Restarting connection")

        val request = Request.Builder()
            .url("$url?channel=$channel&userId=$userId")
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, this)
        Log.d("WebSocketManager", "Connecting to channel=$channel userId=$userId")
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnected")
        webSocket = null
    }

    fun sendAudio(data: ByteArray) {
        val byteString = data.toByteString(0, data.size)
        if (webSocket?.send(byteString) != true) {
            Log.e("WebSocketManager", "WebSocket is null or closed, cannot send audio")
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("WebSocketManager", "WebSocket connection opened")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        val data = bytes.toByteArray()
        CoroutineScope(Dispatchers.IO).launch {
            _incomingAudio.emit(data)
        }
        Log.d("WebSocketManager", "Audio received (${data.size} bytes)")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WebSocketManager", "Text received: $text")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocketManager", "Closing connection: $code - $reason")
        webSocket.close(1000, null)
        this.webSocket = null
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocketManager", "WebSocket closed: $code - $reason")
        this.webSocket = null
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSocketManager", "WebSocket error: ${t.message}", t)
        this.webSocket = null
    }
}
