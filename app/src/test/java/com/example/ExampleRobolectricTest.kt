package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TANAM BOSQUU", appName)
    }

    @Test
    fun `test survival rate calculation formula`() {
        val initialSeedlings = 100
        val survivingCount = 94
        val survivalRate = (survivingCount.toFloat() / initialSeedlings.toFloat()) * 100f
        assertEquals(94.0f, survivalRate, 0.01f)
        assertTrue(survivalRate >= 80f)
    }

    @Test
    fun `test germination rate calculation formula`() {
        val seedsPlanted = 120
        val germinatedCount = 114
        val germinationRate = (germinatedCount.toFloat() / seedsPlanted.toFloat()) * 100f
        assertEquals(95.0f, germinationRate, 0.01f)
    }

    @Test
    fun `test average daily growth calculation`() {
        val prevHeight = 2.4f
        val currentHeight = 8.4f
        val daysBetween = 6
        val dailyGrowth = (currentHeight - prevHeight) / daysBetween
        assertEquals(1.0f, dailyGrowth, 0.01f)
    }
}
