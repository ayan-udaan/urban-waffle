package com.udaan.urbanwaffle.jobs

import com.udaan.common.utils.kotlin.logger

class SampleJob {
    companion object {
        private val LOG by logger()

        @JvmStatic
        fun main(args: Array<String>) {
            try {
                LOG.info("Job STARTED")

                // job logic goes here
            }
            catch (e: Throwable) {
                LOG.error("Job FAILED", e)
                System.exit(1)
            }
            finally {
                LOG.error("Job SUCCESSFUL")
                System.exit(0)
            }
        }
    }
}
