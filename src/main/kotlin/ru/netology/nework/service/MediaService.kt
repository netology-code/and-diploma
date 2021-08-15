package ru.netology.nework.service

import com.cloudinary.Cloudinary
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.netology.nework.dto.Media

@Service
class MediaService(
    @Lazy
    private val cloudinary: Cloudinary,
) {

    fun save(file: MultipartFile): Media =
            cloudinary.uploader()
                .upload(file.bytes, mapOf(
                    "resource_type" to "auto"
                ))
                .let {
                    Media(it.getValue("url").toString())
                }
}
