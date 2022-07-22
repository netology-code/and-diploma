from dataclasses import dataclass
from typing import Optional
from datetime import datetime

from attachments.domain.attachment import Attachment
from coordinates.domain.coordinates_dto import CoordinatesDto
from events.domain.models.event_create_request import EventCreateRequest
from events.domain.models.event_type import EventType
from users.domain.models import UserDto, UserPreviewDto


@dataclass
class EventDto:
    id: int
    authorId: int
    author: str
    authorAvatar: Optional[str]
    content: str
    datetime: datetime
    published: datetime
    coords: Optional[CoordinatesDto]
    type: EventType
    likeOwnerIds: set[int]
    likedByMe: bool
    speakerIds: set[int]
    participantsIds: set[int]
    participatedByMe: bool
    attachment: Optional[Attachment]
    link: Optional[str]
    ownedByMe: bool
    users: dict[int, UserPreviewDto]

    @staticmethod
    def from_request(request: EventCreateRequest, author: UserDto) -> 'EventDto':
        return EventDto(
            id=request.id,
            authorId=author.id,
            author=author.name,
            authorAvatar=author.avatar,
            content=request.content,
            published=datetime.now(),
            coords=request.coords,
            link=request.link,
            likeOwnerIds=set(),
            likedByMe=False,
            attachment=request.attachment,
            datetime=request.datetime,
            participantsIds=set(),
            participatedByMe=False,
            type=request.type,
            speakerIds=request.speakerIds,
            ownedByMe=False,
            likeOwners={},
            participants={},
        )
