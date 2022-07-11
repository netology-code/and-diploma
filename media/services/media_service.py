import os
import uuid
from typing import Union

from django.core.files import File

from imagekitio import ImageKit

from media.domain.media_dto import MediaDto
from nmedia.domain.errors import TextError


class MediaService:
    image_kit: ImageKit

    def __init__(self, image_kit: ImageKit):
        self.image_kit = image_kit

    def save(self, file: File) -> Union[MediaDto, TextError]:
        extension = os.path.splitext(str(file))[1]
        try:
            upload = self.image_kit.upload_file(
                file=file.file,
                file_name=str(uuid.uuid4()) + "." + extension,
            )
            return MediaDto(url=upload['response']['url'])
        except Exception as e:
            return TextError(e.__str__())
