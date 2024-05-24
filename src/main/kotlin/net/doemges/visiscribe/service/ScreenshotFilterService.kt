package net.doemges.visiscribe.service

import net.doemges.visiscribe.model.Screenshot
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ScreenshotFilterService(
    private val entropyCalculationService: EntropyCalculationService
) {

    fun filterScreenshots(flux: Flux<Screenshot>, threshold: Double): Flux<Screenshot> =
        flux.buffer(2, 1) // Buffer of size 2 with step 1 to compare consecutive screenshots
        .filter { buffer ->
            if (buffer.size < 2) {
                true // Keep the first screenshot
            } else {
                val screenshotA = buffer[0]
                val screenshotB = buffer[1]
                val conditionalEntropy = entropyCalculationService.calculateConditionalEntropy(
                    screenshotA.combinedHistogram!!,
                    screenshotB.combinedHistogram!!
                                                                                              )
                conditionalEntropy > threshold
            }
        }
        .map { it.last() } // Emit only the second screenshot in the buffer
}
