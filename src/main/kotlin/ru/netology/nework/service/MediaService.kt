package ru.netology.nework.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.netology.nework.dto.Media
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Service
class MediaService(@Value("\${app.media-location}") private val mediaLocation: String) {
    private val path = Paths.get(mediaLocation)

    fun saveMedia(file: MultipartFile): Media = save("media", file)

    fun saveAvatar(file: MultipartFile): Media = save("avatars", file)

    fun save(folder: String, file: MultipartFile): Media =
        UUID.randomUUID()
            .toString()
            .let(::Media)
            .also { media ->
                Paths.get(folder, media.id)
                    .let(path::resolve)
                    .also { Files.createDirectories(it.parent) }
                    .also(file::transferTo)
            }
}
