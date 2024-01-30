package net.orandja.ktm.benchmark

import net.orandja.ktm.Ktm
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class EqualityCheck {

    @BeforeTest
    fun before() {
        Ktm.setDefaultAdapters(AutoKtmAdaptersModule)
    }

    @Test
    fun `User Check`() {
        with(UserBenchmark()) {
            setup()
            assertEquals(LBriandKtmRender(), SpullaraMustacheRender())
        }
    }

    @Test
    fun `Winner Check`() {
        with(WinnerBenchmark()) {
            setup()
            assertEquals(LBriandKtmRender(), SpullaraMustacheRender())
        }
    }

    @Test
    fun `Winners x1 Check`() {
        with(WinnersBenchmark()) {
            setup()
            assertEquals(LBriandKtmRenderX01(), SpullaraMustacheRenderX01())
        }
    }

    @Test
    fun `Winners x10 Check`() {
        with(WinnersBenchmark()) {
            setup()
            assertEquals(LBriandKtmRenderX10(), SpullaraMustacheRenderX10())
        }
    }

    @Test
    fun `Winners x50 Check`() {
        with(WinnersBenchmark()) {
            setup()
            assertEquals(LBriandKtmRenderX50(), SpullaraMustacheRenderX50())
        }
    }
}