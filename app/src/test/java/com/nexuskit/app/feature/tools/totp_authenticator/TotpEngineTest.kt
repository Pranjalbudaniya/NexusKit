package com.nexuskit.app.feature.tools.totp_authenticator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TotpEngineTest {

    @Test
    fun testStandardRfc6238Vector() {
        // Standard test secret "12345678901234567890" in Base32 = GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ
        val secret = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ"
        // RFC 6238 test vectors: at time 59 (T=1), OTP is 287082
        val code59 = TotpEngine.generateTotp(secret, epochSeconds = 59L, period = 30, digits = 6, algorithm = "SHA1")
        assertTrue(code59.isSuccess)
        assertEquals("287082", code59.getOrThrow())

        // At time 1111111109 (T=37037036), OTP is 081804
        val codeTest2 = TotpEngine.generateTotp(secret, epochSeconds = 1111111109L, period = 30, digits = 6, algorithm = "SHA1")
        assertTrue(codeTest2.isSuccess)
        assertEquals("081804", codeTest2.getOrThrow())
    }

    @Test
    fun testBase32Cleaning() {
        val messy = " jbsw-y3dp  ehpk-3pxp= "
        val clean = TotpEngine.cleanBase32Secret(messy)
        assertEquals("JBSWY3DPEHPK3PXP=", clean)
    }

    @Test
    fun testOtpAuthUriParsing() {
        val uri = "otpauth://totp/GitHub:octocat?secret=JBSWY3DPEHPK3PXP&issuer=GitHub&digits=6&period=30"
        val parsedRes = TotpEngine.parseOtpAuthUri(uri)
        assertTrue(parsedRes.isSuccess)
        val acc = parsedRes.getOrThrow()
        assertEquals("octocat", acc.accountName)
        assertEquals("GitHub", acc.issuer)
        assertEquals("JBSWY3DPEHPK3PXP", acc.secretKey)
        assertEquals(6, acc.digits)
        assertEquals(30, acc.periodSeconds)
    }
}
