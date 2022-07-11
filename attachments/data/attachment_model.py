from django.db import models

from attachments.domain.attachment import Attachment
from attachments.domain.attachment_type import AttachmentType


class AttachmentModel(models.Model):
    url = models.TextField()
    type = models.TextField()

    def to_dto(self) -> Attachment:
        return Attachment(url=self.url, type=AttachmentType.from_str(self.type))
