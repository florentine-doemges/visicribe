package net.doemges.visiscribe.service

import net.doemges.visiscribe.model.Screenshot
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.io.File

@Service
class VideoProcessingService(
    private val screenshotExtractionService: ScreenshotExtractionService,
    private val histogramCalculationService: HistogramCalculationService,
    private val screenshotFilterService: ScreenshotFilterService
) {

    fun processVideo(video: File, msInterval: Int, threshold: Double): Flux<Screenshot> =
        screenshotExtractionService.extractScreenshots(video, msInterval)
            .map { screenshot -> histogramCalculationService.calculateHistogram(screenshot) }
            .transform { flux -> screenshotFilterService.filterScreenshots(flux, threshold) }
}
