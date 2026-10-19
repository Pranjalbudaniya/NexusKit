package com.nexuskit.app.feature.tools.cron_explainer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CronEngineTest {

    @Test
    fun testEveryMinute() {
        val res = CronEngine.explain("* * * * *")
        assertTrue(res.isSuccess)
        val exp = res.getOrThrow()
        assertTrue(exp.humanDescription.contains("Every minute"))
        assertEquals(5, exp.nextRuns.size)
    }

    @Test
    fun testStepCron() {
        val res = CronEngine.explain("*/15 * * * *")
        assertTrue(res.isSuccess)
        val exp = res.getOrThrow()
        assertTrue(exp.humanDescription.contains("Every 15 minutes"))
        assertEquals(5, exp.nextRuns.size)
    }

    @Test
    fun testWeekdayCron() {
        val res = CronEngine.explain("0 9 * * 1-5")
        assertTrue(res.isSuccess)
        val exp = res.getOrThrow()
        assertTrue(exp.humanDescription.contains("9:00 AM"))
        assertTrue(exp.humanDescription.contains("Monday through Friday"))
    }
}
