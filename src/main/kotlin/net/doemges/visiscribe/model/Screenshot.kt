package net.doemges.visiscribe.model

import org.bytedeco.opencv.opencv_core.Mat
import java.awt.image.BufferedImage

data class Screenshot(
    val bufferedImage: BufferedImage,
    val timeMs: Long,
    val frameNumber: Long,
    val width: Int,
    val height: Int,
    val videoFileName: String,
    var histogram: Mat? = null)
