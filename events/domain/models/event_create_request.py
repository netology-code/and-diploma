from dataclasses import dataclass
from typing import Optional
from datetime import datetime

from attachments.domain.attachment import Attachment
from coordinates.domain.coordinates_dto import CoordinatesDto
from events.domain.models.event_type import EventType


@dataclass
class EventCreateRequest:
    id: int
    content: str
    type: Optional[EventType]
    coords: Optional[CoordinatesDto]
    link: Optional[str]
    speaker_ids: Optional[set[int]]
    attachment: Optional[Attachment]
    # В случае создания (id == 0), обязательно
    datetime: Optional[datetime]
