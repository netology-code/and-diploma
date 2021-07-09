package ru.netology.nework.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.netology.nework.dto.Media
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
class MediaService(@Value("\${app.media-location}") private val mediaLocation: String) {
    private val path = Path.of(mediaLocation)

    fun saveMedia(file: MultipartFile): Media = save("media", file)

    fun saveAvatar(file: MultipartFile): Media = save("avatars", file)

    fun save(folder: String, file: MultipartFile): Media {
        val id = UUID.randomUUID().toString()
        file.transferTo(path.resolve(Paths.get(folder, id)))
        return Media(id)
    }
}
