package watermark

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.lang.IndexOutOfBoundsException
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun saveImage(image: BufferedImage, imageFile: File, format: String) {
    ImageIO.write(image, format, imageFile)
}

fun getFormat(s: String): String {
    val reversedS = s.reversed()
    var char = reversedS[0]
    var output = ""
    var index = 0
    while(char != '.') {
        output += char
        index++
        char = reversedS[index]
    }
    return output.reversed()
}

fun getImage(imageName: String): BufferedImage? {

    val imgName = if (imageName == "watermark") {
        "watermark image"
    }
    else {
        imageName
    }

    println("Input the $imgName filename:")
    val input = readln()
    val file = File(input)
    if (file.exists()) {
        val image = ImageIO.read(file)

        if (image.colorModel.numColorComponents == 3) {
            if(image.colorModel.pixelSize == 24 || image.colorModel.pixelSize == 32) {
                return image
            } else {
                println("The $imageName isn't 24 or 32-bit.")
            }
        } else {
            println("The number of $imageName color components isn't 3.")
        }
    } else {
        println("The file ${file.path} doesn't exist.")
    }
    return null
}

fun compareImagesSize(img1: BufferedImage, img2: BufferedImage): Pair<String, Boolean> {
    var message = ""
    var condition = false
    if(img1.height != img2.height || img1.width != img2.width) {
        condition = true
        if(img1.height < img2.height || img1.width < img2.width)
            message = "The watermark's dimensions are larger."
    }

    return Pair(message, condition)
}

fun merge(originalImg: BufferedImage, oldWatermark: BufferedImage) {
    val watermark = newWatermark(originalImg, oldWatermark)
    println("Input the watermark transparency percentage (Integer 0-100):")
    try {
        val weight = readln().toInt()
        if (weight in 0..100) {
            println("Input the output image filename (jpg or png extension):")
            val resultImageName = readln()
            if(resultImageName.contains(".jpg") || resultImageName.contains(".png")) {
                val width = originalImg.width
                val height = originalImg.height
                val mergedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
                for (x in 0 until width)
                    for (y in 0 until height) {
                        val i = Color(originalImg.getRGB(x, y))
                        val w = Color(watermark.getRGB(x, y))
                        val newColor = Color(
                            (weight * w.red + (100 - weight  ) * i.red) / 100,
                            (weight * w.green + (100 - weight) * i.green) / 100,
                            (weight * w.blue + (100 - weight ) * i.blue) / 100
                        )
                        mergedImage.setRGB(x, y, newColor.rgb)
                    }
                val file = File(resultImageName)
                val format = getFormat(resultImageName)
                saveImage(mergedImage, file, format)
                println("The watermarked image ${file.path} has been created.")
            } else {
                println("The output file extension isn't \"jpg\" or \"png\".")
            }
        } else {
            println("The transparency percentage is out of range.")
        }
    } catch (e: Exception) {
        println("The transparency percentage isn't an integer number.")
    }
}

fun mergeTransparent(originalImg: BufferedImage, oldWatermark: BufferedImage) {

    println("Input the watermark transparency percentage (Integer 0-100):")
    try {
        val weight = readln().toInt()
        if (weight in 0..100) {
            val watermark = newWatermark(originalImg, oldWatermark)
            println("Input the output image filename (jpg or png extension):")
            val resultImageName = readln()
            if(resultImageName.contains(".jpg") || resultImageName.contains(".png")) {
                val width = originalImg.width
                val height = originalImg.height
                val mergedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
                for (x in 0 until width)
                    for (y in 0 until height) {
                        val i = Color(originalImg.getRGB(x, y))
                        val w = Color(watermark.getRGB(x, y), true)

                        val newColor = if(w.alpha == 255)
                            Color(
                                (weight * w.red + (100 - weight) * i.red) / 100,
                                (weight * w.green + (100 - weight) * i.green) / 100,
                                (weight * w.blue + (100 - weight) * i.blue) / 100
                            )
                        else
                            i

                        mergedImage.setRGB(x, y, newColor.rgb)
                    }
                val file = File(resultImageName)
                val format = getFormat(resultImageName)
                saveImage(mergedImage, file, format)
                println("The watermarked image ${file.path} has been created.")
            } else {
                println("The output file extension isn't \"jpg\" or \"png\".")
            }
        } else {
            println("The transparency percentage is out of range.")
        }
    } catch (e: Exception) {
        println("The transparency percentage isn't an integer number.")
    }
}

fun mergeTransparentBackground(originalImg: BufferedImage, oldWatermark: BufferedImage, tColor: Color) {

    println("Input the watermark transparency percentage (Integer 0-100):")
    try {
        val weight = readln().toInt()
        if (weight in 0..100) {
            val watermark = newWatermark(originalImg, oldWatermark)
            println("Input the output image filename (jpg or png extension):")
            val resultImageName = readln()
            if(resultImageName.contains(".jpg") || resultImageName.contains(".png")) {
                val width = originalImg.width
                val height = originalImg.height
                val mergedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
                for (x in 0 until width)
                    for (y in 0 until height) {
                        val i = Color(originalImg.getRGB(x, y))
                        val w = Color(watermark.getRGB(x, y), true)

                        val newColor = if(w != tColor)
                            Color(
                                (weight * w.red + (100 - weight) * i.red) / 100,
                                (weight * w.green + (100 - weight) * i.green) / 100,
                                (weight * w.blue + (100 - weight) * i.blue) / 100
                            )
                        else
                            i

                        mergedImage.setRGB(x, y, newColor.rgb)
                    }
                val file = File(resultImageName)
                val format = getFormat(resultImageName)
                saveImage(mergedImage, file, format)
                println("The watermarked image ${file.path} has been created.")
            } else {
                println("The output file extension isn't \"jpg\" or \"png\".")
            }
        } else {
            println("The transparency percentage is out of range.")
        }
    } catch (e: Exception) {
        println("The transparency percentage isn't an integer number.")
    }
}

fun positionWatermark(originalImage: BufferedImage, watermark: BufferedImage, position: List<Int>): BufferedImage {
    val newWatermark = BufferedImage(originalImage.width, originalImage.height, BufferedImage.TYPE_INT_ARGB)
    val posX = position.first()
    val posY = position.last()
    for (x in 0 until newWatermark.width)
        for (y in 0 until newWatermark.height) {
            if(x > posX && y > posY && x - posX < watermark.width && y - posY < watermark.height) {
                newWatermark.setRGB(x, y, watermark.getRGB(x - posX, y - posY))
            } else {
                newWatermark.setRGB(x, y, originalImage.getRGB(x, y))
            }
        }
    return newWatermark
}

fun gridWatermark(originalImage: BufferedImage, watermark: BufferedImage): BufferedImage {
    val newWatermark = BufferedImage(originalImage.width, originalImage.height, BufferedImage.TYPE_INT_ARGB)
    val wh = watermark.height
    val ww = watermark.width
    for (x in 0 until newWatermark.width)
        for (y in 0 until newWatermark.height) {

            newWatermark.setRGB(x, y, watermark.getRGB(x % ww, y % wh))
        }
    return newWatermark
}

fun newWatermark(originalImage: BufferedImage, watermark: BufferedImage): BufferedImage {
    println("Choose the position method (single, grid):")
    when(readln()) {
        "single" -> {
            println("Input the watermark position ([x 0-${originalImage.width - watermark.width}]" +
                    " [y 0-${originalImage.height - watermark.height}]):")
            try {
                val position = readln().split(' ').map{ it.toInt() }
                if(position.first() !in 0..originalImage.width - watermark.width ||
                    position.last() !in 0..originalImage.height - watermark.height ||
                    position.size != 2) {
                    println("The position input is out of range.")
                    exitProcess(1)
                } else {
                    return positionWatermark(originalImage, watermark, position)
                }
            } catch(e: Exception) {
                println("The position input is invalid.")
                exitProcess(1)
            }
        }
        "grid" -> {
            return gridWatermark(originalImage, watermark)
        }
        else -> {
            println("The position method input is invalid.")
            exitProcess(1)
        }
    }
}

fun runProgram() {
    val originalImage = getImage("image") ?: return
    val watermark = getImage("watermark") ?: return

    if(compareImagesSize(originalImage, watermark).second) {
        println(compareImagesSize(originalImage, watermark).first)
        if(compareImagesSize(originalImage, watermark).first == "The watermark's dimensions are larger.")
            return
    }

    when(watermark.transparency) {
        3 -> {
            println("Do you want to use the watermark's Alpha channel?")
            val tmpInput = readln()
            if (tmpInput == "yes") {
                mergeTransparent(originalImage, watermark)
            } else {
                merge(originalImage, watermark)
            }
        }
        else -> {
            println("Do you want to set a transparency color?")

            val tmpInput = readln()
            if (tmpInput == "yes") {
                val newColor: Color
                println("Input a transparency color ([Red] [Green] [Blue]):")
                try {
                    val inputColor = readln().split(' ').map{ it.toInt() }
                    if(inputColor.size > 3) throw IndexOutOfBoundsException()
                    newColor = Color(
                        inputColor[0],
                        inputColor[1],
                        inputColor[2]
                    )
                } catch(e: Exception) {
                    println("The transparency color input is invalid.")
                    return
                }
                mergeTransparentBackground(originalImage, watermark, newColor)
            } else {
                merge(originalImage, watermark)
            }
        }
    }
}

fun main() {
    runProgram()
}