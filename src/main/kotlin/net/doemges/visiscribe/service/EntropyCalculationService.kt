package net.doemges.visiscribe.service

import net.doemges.visiscribe.model.Screenshot
import org.bytedeco.opencv.opencv_core.Mat
import org.springframework.stereotype.Service

@Service
class EntropyCalculationService {

    fun calculateConditionalEntropy(histogramA: Mat, histogramB: Mat): Double {
        val bins = histogramA.rows()
        var conditionalEntropy = 0.0

        for (i in 0 until bins) {
            val pA = histogramA.ptr(i).getDouble(0)
            val pB = histogramB.ptr(i).getDouble(0)

            if (pA > 0 && pB > 0) {
                conditionalEntropy += pB * Math.log(pB / pA) / Math.log(2.0)
            }
        }

        return -conditionalEntropy
    }
}
