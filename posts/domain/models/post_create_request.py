from dataclasses import dataclass
from typing import Optional

from attachments.domain.attachment import Attachment
from coordinates.domain.coordinates_dto import CoordinatesDto


@dataclass
class PostCreateRequest:
    id: int
    content: str
    coords: Optional[CoordinatesDto]
    link: Optional[str]
    attachment: Optional[Attachment]
