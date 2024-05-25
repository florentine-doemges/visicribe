package net.doemges.visiscribe.service

import kotlinx.coroutines.reactor.mono
import net.doemges.visiscribe.model.Screenshot
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.io.File

@Service
class VideoProcessingService(
    private val frameExtractionService: FrameExtractionService,
    private val histogramService: HistogramService,
    private val frameAnalysisService: FrameAnalysisService
                            ) {

    fun processVideo(videoFile: File, intervalMs: Int): Flux<Screenshot> {
        return frameExtractionService.extractFrames(videoFile, intervalMs)
            .flatMap { screenshot ->
                mono { screenshot.apply { histogram = histogramService.calculateHistogram(bufferedImage) } }
            }
            .buffer(2, 1)
            .filter { it.size == 2 }
            .map { frameAnalysisService.calculateJSDivergence(it[0].histogram!!, it[1].histogram!!) to it[1] }
            .scan(mutableListOf<Screenshot>()) { acc, pair ->
                val (divergence, screenshot) = pair
                val averageDivergence = frameAnalysisService.calculateAverageJSDivergence(acc)
                if (acc.isEmpty() || kotlin.math.abs(divergence - averageDivergence) < averageDivergence * 0.1) {
                    acc.apply { add(screenshot) }
                } else {
                    acc
                }
            }
            .flatMapIterable { it }
    }
}
