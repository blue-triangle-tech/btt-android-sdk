package com.bluetriangle.analytics;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import androidx.annotation.NonNull;

/**
 *
 */
final class Payload {

    /**
     * The UUID id for this payload, used in cache filename
     */
    @NonNull final String id;

    /**
     * The number of attempts this payload has been tried to be sent
     */
    final int payloadAttempts;

    /**
     * The URL to send this payload
     */
    @NonNull final String url;

    /**
     * The actual payload to send, base64 encoded
     */
    @NonNull final String data;

    public Payload(@NonNull final String url, @NonNull final String data) {
        this.id = UUID.randomUUID().toString();
        payloadAttempts = 0;
        this.url = url;
        this.data = data;
    }

    private Payload(@NonNull String id, int payloadAttempts, @NonNull String url, @NonNull String data) {
        this.id = id;
        this.payloadAttempts = payloadAttempts;
        this.url = url;
        this.data = data;
    }

    public void serialize(@NonNull final File directory) throws IOException {
        final File payloadFile = new File(directory, id);
        try (final DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(payloadFile)))) {
            outStream.writeUTF(id);
            // auto-increment payload attempts on each write
            outStream.writeInt(payloadAttempts + 1);
            outStream.writeUTF(url);
            outStream.writeUTF(data);
            outStream.flush();
        } catch (IOException e) {
            throw e;
        }
    }

    @NonNull
    public static Payload deserialize(@NonNull final File payloadFile) throws IOException {
        try (final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(payloadFile)))) {
            final String id = inputStream.readUTF();
            final int payloadAttempts = inputStream.readInt();
            final String url = inputStream.readUTF();
            final String payloadData = inputStream.readUTF();
            return new Payload(id, payloadAttempts, url, payloadData);
        } catch (IOException e) {
            throw e;
        }
    }

}
