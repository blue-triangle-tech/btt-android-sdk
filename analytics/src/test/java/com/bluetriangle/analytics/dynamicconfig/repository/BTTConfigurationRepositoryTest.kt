package com.bluetriangle.analytics.dynamicconfig.repository

import android.content.Context
import android.content.SharedPreferences
import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import kotlin.math.absoluteValue

@RunWith(RobolectricTestRunner::class)
class BTTConfigurationRepositoryTest {

    @Mock
    lateinit var sharedPreferences: SharedPreferences

    @Mock
    lateinit var mockContext: Context

    @Mock
    lateinit var editor: SharedPreferences.Editor

    private lateinit var preferencesMap: MutableMap<String, Any>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Initialize the map to simulate SharedPreferences
        preferencesMap = mutableMapOf()

        // Mock the editor's behavior to put values into the map
        whenever(sharedPreferences.edit()).thenReturn(editor)

        whenever(editor.putString(any(), anyOrNull())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            val value = invocation.getArgument<String>(1)
            preferencesMap[key] = value
            editor
        }

        whenever(editor.commit()).thenReturn(true)

        // Mock retrieval methods to return values from the map
        whenever(sharedPreferences.getString(any(), anyOrNull())).thenAnswer { invocation ->
            val key = invocation.getArgument<String>(0)
            preferencesMap[key] as? String ?: invocation.getArgument(1)
        }

        whenever(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)
    }

    @Test
    fun `When configuration is present in cache should return cached configuration`() {
        val testSampleRate = Math.random()
        val config = BTTRemoteConfiguration(testSampleRate)
        val repository = BTTConfigurationRepository(mockContext)
        repository.save(config)

        assertEquals(
            "Network sample rate doesn't match",
            testSampleRate,
            BTTConfigurationRepository(mockContext).get()?.networkSampleRate ?: 0.0,
            0.000005
        )
    }

    @Test
    fun `When cache is empty should return null`() {
        val repository = BTTConfigurationRepository(mockContext)

        assertEquals(
            "Doesn't return null",
            null,
            repository.get()
        )
    }

    @Test
    fun `Configuration should be saved with correct timestamp`() {
        val repository = BTTConfigurationRepository(mockContext)

        val config = BTTRemoteConfiguration(Math.random())
        repository.save(config)

        val currentTime = System.currentTimeMillis()
        assertTrue(
            "Saved date is not correct! $currentTime : ${repository.get()?.savedDate}",
            (currentTime - (repository.get()?.savedDate ?: 0L)).absoluteValue <= 100
        )
    }
}