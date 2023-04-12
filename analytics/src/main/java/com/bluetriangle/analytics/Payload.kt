package com.bluetriangle.analytics

import java.io.*
import java.util.*

data class Payload(
    val id: String = UUID.randomUUID().toString(), // The UUID id for this payload, used in cache filename
    val payloadAttempts: Int = 0, // The number of attempts this payload has been tried to be sent
    val url: String, // The URL to send this payload
    val data: String, // The actual payload to send, base64 encoded
) {
    @Throws(IOException::class)
    fun serialize(directory: File) {
        val payloadFile = File(directory, id)
        try {
            DataOutputStream(BufferedOutputStream(FileOutputStream(payloadFile))).use { outStream ->
                outStream.writeUTF(id)
                // auto-increment payload attempts on each write
                outStream.writeInt(payloadAttempts + 1)
                outStream.writeUTF(url)
                outStream.writeUTF(data)
                outStream.flush()
            }
        } catch (e: IOException) {
            throw e
        }
    }

    companion object {
        @Throws(IOException::class)
        fun deserialize(payloadFile: File): Payload {
            try {
                DataInputStream(BufferedInputStream(FileInputStream(payloadFile))).use { inputStream ->
                    val id = inputStream.readUTF()
                    val payloadAttempts = inputStream.readInt()
                    val url = inputStream.readUTF()
                    val payloadData = inputStream.readUTF()
                    return Payload(id, payloadAttempts, url, payloadData)
                }
            } catch (e: IOException) {
                throw e
            }
        }
    }
}