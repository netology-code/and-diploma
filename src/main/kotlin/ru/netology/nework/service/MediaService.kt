package ru.netology.nework.service

import io.imagekit.sdk.ImageKit
import io.imagekit.sdk.models.FileCreateRequest
import java.util.UUID
import org.apache.tika.Tika
import org.springframework.data.util.Lazy
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import org.springframework.web.multipart.MultipartFile
import ru.netology.nework.dto.Media
import ru.netology.nework.exception.BadContentTypeException

@Service
class MediaService(
    private val imageKit: Lazy<ImageKit>,
) {
    private val tika = Tika()

    fun save(file: MultipartFile): Media {
        val mediaType: String = tika.detect(file.inputStream)
        return imageKit.get()
            .upload(
                FileCreateRequest(
                    file.bytes, "${UUID.randomUUID()}.${
                        when (mediaType) {
                            MimeTypeUtils.IMAGE_JPEG_VALUE -> "jpg"
                            MimeTypeUtils.IMAGE_PNG_VALUE -> "png"
                            "audio/mpeg" -> "mp3"
                            "video/quicktime", 
                            "application/mp4" -> "mp4"
                            else -> throw BadContentTypeException()
                        }
                    }"
                )
            )
            .let {
                Media(it.url.toString())
            }
    }
}
