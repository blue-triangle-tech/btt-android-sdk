package com.bluetriangle.analytics.caching.serializer

import com.bluetriangle.analytics.Logger
import com.bluetriangle.analytics.Payload
import com.bluetriangle.analytics.caching.classifier.Classifier
import com.bluetriangle.analytics.utility.isDirectoryInvalid
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class PayloadSerializer(
    private val logger: Logger?,
    private val directory: File,
    private val classifier: Classifier
) : Serializer {

    override fun serialize(payload: Payload) {
        if(directory.isDirectoryInvalid) return

        val payloadFile = File(directory, "${payload.id}.${payload.type.extension}")
        payloadFile.length()
        try {
            DataOutputStream(BufferedOutputStream(FileOutputStream(payloadFile))).use { outStream ->
                outStream.writeUTF(payload.id)
                // auto-increment payload attempts on each write
                outStream.writeInt(payload.payloadAttempts)
                outStream.writeUTF(payload.url)
                outStream.writeUTF(payload.data)
                outStream.writeLong(payload.createdAt)
                outStream.flush()
            }
        } catch (e: IOException) {
            logger?.error("Failed to serialize payload: ${e.message}, Payload: ${payload}")
        }
    }

    override fun deserialize(file: File): Payload? {
        if(directory.isDirectoryInvalid) return null

        try {
            DataInputStream(BufferedInputStream(FileInputStream(file))).use { inputStream ->
                val id = inputStream.readUTF()
                val payloadAttempts = inputStream.readInt()
                val url = inputStream.readUTF()
                val payloadData = inputStream.readUTF()
                val createdAt = inputStream.readLong()
                return Payload(id, payloadAttempts, url, payloadData, classifier.classify(file), createdAt)
            }
        } catch (e: IOException) {
            logger?.error("Failed to deserialize payload: ${e.message}")
            return null
        }
    }
}