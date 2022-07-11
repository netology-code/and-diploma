from dataclasses import dataclass

from attachments.domain.attachment_type import AttachmentType


@dataclass
class Attachment:
    url: str
    type: AttachmentType
