package com.bluetriangle.analytics;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final class PayloadCache {
    private final BlueTriangleConfiguration configuration;
    private final @NonNull File directory;
    private final int maxSize;

    public PayloadCache(BlueTriangleConfiguration configuration) {
        this.configuration = configuration;
        directory = new File(configuration.getCacheDirectory());
        if (isDirectoryValid()) {
            maxSize = configuration.getMaxCacheItems();
        } else {
            maxSize = 0;
        }
    }

    /**
     * Get the next cached payload to attempt resending, if any
     *
     * @return next payload to send or null if none
     */
    @Nullable
    public synchronized Payload getNextCachedPayload() {
        final File[] files = getAllCacheFiles(true);
        if (files.length > 0) {
            final File file = files[0];
            final Payload payload = readPayload(file);
            if(!file.delete()) {
                configuration.getLogger().error("error deleting next cached payload file %s", file.getName());
            }
            return payload;
        }
        return null;
    }

    /**
     * Check to see if there are any cached payloads that need to still be sent
     *
     * @return true if there are existing payload cache files, else false
     */
    public boolean hasCachedPayloads() {
        if (maxSize > 0) {
            return getAllCacheFiles(false).length > 0;
        }
        return false;
    }

    /**
     * Clears the cache by deleting all cache files in the cache directory
     */
    public void clearCache() {
        for (final File file : getAllCacheFiles(false)) {
            if (!file.delete()) {
                configuration.getLogger().error("Error deleting %s while clearing cache", file.getName());
            }
        }
    }

    public void cachePayload(@NonNull final Payload payload) {
        if (maxSize > 0) {
            if (payload.payloadAttempts >= configuration.getMaxAttempts()) {
                configuration.getLogger().warn("Payload %s has exceeded max attempts %s", payload.id, payload.payloadAttempts);
                return;
            }
            try {
                payload.serialize(directory);
            } catch (IOException e) {
                configuration.getLogger().error(e, "Failed to cache payload %s", payload.id);
            }
            rotateCacheIfNeeded();
        }
    }

    @Nullable
    public Payload readPayload(@NonNull final File file) {
        try {
            return Payload.deserialize(file);
        } catch (IOException e) {
            configuration.getLogger().error(e, "Failed to load payload %s", file.getAbsolutePath());
            if (!file.delete()) {
                configuration.getLogger().warn("Could not delete payload file %s", file.getAbsolutePath());
            }
        }
        return null;
    }

    /**
     * Get all cache files, sorted oldest to newest
     *
     * @return all cached
     */
    @NonNull
    private File[] getAllCacheFiles(final boolean sort) {
        if (isDirectoryValid()) {
            // lets filter the session.json here
            final File[] files = directory.listFiles();
            if (files != null) {
                if (sort) {
                    sortFilesOldestToNewest(files);
                }
                return files;
            }
        }
        return new File[]{};
    }

    /**
     * Check if a dir. is valid and have write and read permission
     *
     * @return true if valid and has permissions or false otherwise
     */
    private boolean isDirectoryValid() {
        if (!directory.isDirectory() || !directory.canWrite() || !directory.canRead()) {
            configuration.getLogger().error("The directory for caching files is not valid: %s", directory.getAbsolutePath());
            return false;
        }
        return true;
    }

    /**
     * Sort files from oldest to the newest using the lastModified method
     *
     * @param files the Files
     */
    private void sortFilesOldestToNewest(@NonNull File[] files) {
        // just sort it if more than 1 file
        if (files.length > 1) {
            Arrays.sort(files, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
        }
    }

    /**
     * Rotates the caching folder if full, deleting the oldest files first
     */
    void rotateCacheIfNeeded() {
        final File[] files = getAllCacheFiles(true);
        final int length = files.length;
        if (length >= maxSize) {
            configuration.getLogger().warn("Cache folder size %s > %s. Rotating files.", length, maxSize);
            final int totalToBeDeleted = (length - maxSize) + 1;

            sortFilesOldestToNewest(files);

            // delete files from the top of the Array as its sorted by the oldest to the newest
            for (int i = 0; i < totalToBeDeleted; i++) {
                final File file = files[i];
                if (!file.delete()) {
                    configuration.getLogger().warn("Error deleting file: %s", file.getAbsolutePath());
                }
            }
        }
    }
}
