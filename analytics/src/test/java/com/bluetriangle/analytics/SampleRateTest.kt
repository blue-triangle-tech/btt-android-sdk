package com.bluetriangle.analytics

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import kotlin.math.abs


@RunWith(Parameterized::class)
class SampleRateTest {

    class SampleRateExpectation(
        val sampleRate: Double
    ) {
        fun isInRange(actualRate: Double): Boolean {
            return abs(actualRate - sampleRate) <= 0.1
        }

        override fun toString(): String {
            return sampleRate.toString()
        }
    }

    companion object {
        @Parameters(name = "{index}: SampleRate: {0}")
        @JvmStatic
        public fun sampleRates(): List<SampleRateExpectation> {
            val tests = arrayOf(
                SampleRateExpectation(0.25),
                SampleRateExpectation(0.75),
                SampleRateExpectation(0.5)
            )
            return listOf(
                *tests,
                *tests,
                *tests,
                *tests,
                *tests
            )
        }
    }

    @Parameter
    public lateinit var sampleRateExpectation: SampleRateExpectation

    @Test
    fun `Test sample rate `() {
        var countMidRangeTrues = 0
        for (i in 0 until 100) {
            if (Utils.shouldSample(sampleRateExpectation.sampleRate)) {
                countMidRangeTrues++
            }
        }
        val actualSampleRate = countMidRangeTrues / 100.0
        Assert.assertTrue(
            "Sample rate is not correct, expected: ${sampleRateExpectation.sampleRate}, actual: $actualSampleRate",
            sampleRateExpectation.isInRange(actualSampleRate)
        )
        println("Expected Sample Rate: ${sampleRateExpectation.sampleRate}")
        println("Actual Capture Rate: $actualSampleRate ($countMidRangeTrues/100)")
    }

}