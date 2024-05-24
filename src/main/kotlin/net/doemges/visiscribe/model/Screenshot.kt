package net.doemges.visiscribe.model

import org.bytedeco.opencv.opencv_core.Mat
import java.awt.image.BufferedImage
import kotlin.math.ln

data class Screenshot(
    val bufferedImage: BufferedImage,
    val timeMs: Long,
    val frameNumber: Long,
    val width: Int,
    val height: Int,
    val videoFileName: String,
    var combinedHistogram: Mat? = null
                     ) {
    fun calculateDifferentialEntropy(other: Screenshot): Double {
        requireNotNull(combinedHistogram) { "Combined histogram is null" }
        requireNotNull(other.combinedHistogram) { "Other combined histogram is null" }

        return calculateEntropy(combinedHistogram!!, other.combinedHistogram!!)
    }

    private fun calculateEntropy(hist1: Mat, hist2: Mat): Double {
        val bins = hist1.rows()
        var entropy = 0.0

        for (i in 0 until bins) {
            val p1 = hist1.ptr(i).getDouble(0)
            val p2 = hist2.ptr(i).getDouble(0)

            if (p1 > 0 && p2 > 0) {
                entropy += p1 * ln(p1 / p2)
            }
        }

        return -entropy
    }
}